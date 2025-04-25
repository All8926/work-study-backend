package com.app.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.app.project.constant.CommonConstant;
import com.app.project.constant.UserConstant;
import com.app.project.mapper.JobPostMapper;
import com.app.project.model.dto.jobPost.JobPostQueryRequest;
import com.app.project.model.entity.JobPost;
import com.app.project.model.entity.JobPost;
import com.app.project.model.entity.User;
import com.app.project.model.enums.JobPostStatusEnum;
import com.app.project.model.vo.JobPostVO;
import com.app.project.model.vo.JobPostVO;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【job_post(岗位信息)】的数据库操作Service实现
* @createDate 2025-04-25 13:24:49
*/
@Service
public class JobPostServiceImpl extends ServiceImpl<JobPostMapper, JobPost>
    implements JobPostService {

    @Resource
    private UserService userService;

    @Override
    public QueryWrapper<JobPost> getQueryWrapper(JobPostQueryRequest jobPostQueryRequest, User loginUser) {
        QueryWrapper<JobPost> queryWrapper = new QueryWrapper<>();
        if (jobPostQueryRequest == null) {
            return queryWrapper;
        }
        // 取值
        Long id = jobPostQueryRequest.getId();
        String title = jobPostQueryRequest.getTitle();
        Integer status = jobPostQueryRequest.getStatus();
         String description = jobPostQueryRequest.getDescription();
         String salary = jobPostQueryRequest.getSalary();
         String workAddress = jobPostQueryRequest.getWorkAddress();

        String sortField = jobPostQueryRequest.getSortField();
        String sortOrder = jobPostQueryRequest.getSortOrder();

        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.like(StringUtils.isNotBlank(salary), "salary", salary);
        queryWrapper.like(StringUtils.isNotBlank(workAddress), "workAddress", workAddress);

        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);



        // 企业用户只能获取自己的
        String userRole = loginUser.getUserRole();
        if (userRole.equals(UserConstant.ENTERPRISE_ROLE)) {
            queryWrapper.eq("userId", loginUser.getId());
        }
        // 用户只能获取已发布的
        if (userRole.equals(UserConstant.DEFAULT_ROLE)) {
            queryWrapper.eq("status", JobPostStatusEnum.HAVE_PUBLISHED.getValue());
        }
        // 非用户才可以按状态查询
        if(!userRole.equals(UserConstant.DEFAULT_ROLE)){
            queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        }

        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_DESC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<JobPostVO> getJobPostVOPage(Page<JobPost> jobPostPage) {
        List<JobPost> jobPostList = jobPostPage.getRecords();
        Page<JobPostVO> jobPostVOPage = new Page<>(jobPostPage.getCurrent(), jobPostPage.getSize(), jobPostPage.getTotal());
        if (CollUtil.isEmpty(jobPostList)) {
            return jobPostVOPage;
        }
        // JobPost => JobPostVO
        List<JobPostVO> jobPostVOList = jobPostList.stream().map(item -> {
            JobPostVO jobPostVO = new JobPostVO();
            BeanUtils.copyProperties(item, jobPostVO);
            return jobPostVO;
        }).collect(Collectors.toList());

        // 1. 关联查询用户信息
        Set<Long> userIdSet = jobPostList.stream().map(JobPost::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        jobPostVOList.forEach(jobPostVO -> {
            Long userId = jobPostVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            jobPostVO.setUser(userService.getUserVO(user));
        });
        // endregion

        jobPostVOPage.setRecords(jobPostVOList);
        return jobPostVOPage;
    }
}




