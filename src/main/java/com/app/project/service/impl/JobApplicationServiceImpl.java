package com.app.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.app.project.constant.CommonConstant;
import com.app.project.constant.UserConstant;
import com.app.project.mapper.JobApplicationMapper;
import com.app.project.model.dto.jobApplication.JobApplicationQueryRequest;
import com.app.project.model.entity.JobApplication;
import com.app.project.model.entity.JobApplication;
import com.app.project.model.entity.JobPost;
import com.app.project.model.entity.User;
import com.app.project.model.enums.JobApplicationStatusEnum;
import com.app.project.model.vo.JobApplicationVO;
import com.app.project.model.vo.JobApplicationVO;
import com.app.project.model.vo.UserVO;
import com.app.project.service.JobApplicationService;

import com.app.project.service.JobPostService;
import com.app.project.service.UserService;
import com.app.project.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Administrator
 * @description 针对表【job_application(岗位申请)】的数据库操作Service实现
 * @createDate 2025-04-27 19:40:13
 */
@Service
public class JobApplicationServiceImpl extends ServiceImpl<JobApplicationMapper, JobApplication> implements JobApplicationService {

    @Resource
    private JobPostService jobPostService;

    @Resource
    private UserService userService;

    @Override
    public QueryWrapper<JobApplication> getQueryWrapper(JobApplicationQueryRequest jobApplicationQueryRequest, User loginUser) {
        QueryWrapper<JobApplication> queryWrapper = new QueryWrapper<>();
        if (jobApplicationQueryRequest == null) {
            return queryWrapper;
        }
        // 取值
        Long id = jobApplicationQueryRequest.getId();
        String jobName = jobApplicationQueryRequest.getJobName();
        Integer status = jobApplicationQueryRequest.getStatus();

        String sortField = jobApplicationQueryRequest.getSortField();
        String sortOrder = jobApplicationQueryRequest.getSortOrder();


        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);

        // 岗位名称模糊查询
        if (StringUtils.isNotBlank(jobName)) {
            QueryWrapper<JobPost> jobPostQueryWrapper = new QueryWrapper<>();
            jobPostQueryWrapper.like("title", jobName);
            Set<Long> jobPostIds = jobPostService.list(jobPostQueryWrapper).stream().map(JobPost::getId).collect(Collectors.toSet());
            queryWrapper.in(CollUtil.isNotEmpty(jobPostIds), "jobId", jobPostIds);

        }


        // 企业用户只能获取自己岗位的申请
        String userRole = loginUser.getUserRole();
        if (userRole.equals(UserConstant.ENTERPRISE_ROLE)) {
            queryWrapper.eq("enterpriseId", loginUser.getId());
        }

        // 用户只能获取自己申请的
        if (userRole.equals(UserConstant.DEFAULT_ROLE)) {
            queryWrapper.eq("userId", loginUser.getId());
        }


        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_DESC), sortField);
        return queryWrapper;
    }

    @Override
    public Page<JobApplicationVO> getJobApplicationVOPage(Page<JobApplication> jobApplicationPage) {
        List<JobApplication> jobApplicationList = jobApplicationPage.getRecords();
        Page<JobApplicationVO> jobApplicationVOPage = new Page<>(jobApplicationPage.getCurrent(), jobApplicationPage.getSize(), jobApplicationPage.getTotal());
        if (CollUtil.isEmpty(jobApplicationList)) {
            return jobApplicationVOPage;
        }

        // 1. 批量查询用户（包括用户本人和企业用户）
        Set<Long> userIdSet = jobApplicationList.stream().flatMap(item -> Stream.of(item.getUserId(), item.getEnterpriseId())).collect(Collectors.toSet());

        Map<Long, User> userIdUserMap = userService.listByIds(userIdSet).stream().collect(Collectors.toMap(User::getId, Function.identity()));

        // 2. 批量查询岗位
        Set<Long> jobIdSet = jobApplicationList.stream().map(JobApplication::getJobId).collect(Collectors.toSet());

        Map<Long, JobPost> jobPostIdJobPostMap = jobPostService.listByIds(jobIdSet).stream().collect(Collectors.toMap(JobPost::getId, Function.identity()));

        // 3. 转换为VO
        List<JobApplicationVO> jobApplicationVOList = jobApplicationList.stream().map(item -> {
            JobApplicationVO vo = JobApplicationVO.objToVo(item);

            // 填充用户信息
            User user = userIdUserMap.get(item.getUserId());
            vo.setUser(user != null ? userService.getUserVO(user) : null);

            // 填充企业信息
            User enterprise = userIdUserMap.get(item.getEnterpriseId());
            vo.setEnterprise(enterprise != null ? userService.getUserVO(enterprise) : null);

            // 填充岗位信息
            JobPost jobPost = jobPostIdJobPostMap.get(item.getJobId());
            vo.setJob(jobPost != null ? jobPostService.getJobPostVO(jobPost) : null);

            return vo;
        }).collect(Collectors.toList());

        jobApplicationVOPage.setRecords(jobApplicationVOList);
        return jobApplicationVOPage;
    }

    @Override
    public List<UserVO> getInterviewPassedUser() {

        User loginUser = userService.getLoginUser();
        String userRole = loginUser.getUserRole();
        List<UserVO> userVOList = new ArrayList<>();

        // 获取本公司所有通过面试申请的用户
        QueryWrapper<JobApplication> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enterpriseId", loginUser.getId());
        queryWrapper.eq("status", JobApplicationStatusEnum.INTERVIEW_PASSED.getValue());
        List<JobApplication> jobApplicationList = list(queryWrapper);
        if (CollUtil.isEmpty(jobApplicationList)) {
            return userVOList;
        }
        Set<Long> userIdSet = jobApplicationList.stream().map(JobApplication::getUserId).collect(Collectors.toSet());
        List<User> userList = userService.listByIds(userIdSet);
        userVOList = userService.getUserVO(userList);
        return userVOList;
    }

}




