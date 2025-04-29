package com.app.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.app.project.common.ErrorCode;
import com.app.project.constant.CommonConstant;
import com.app.project.constant.UserConstant;
import com.app.project.exception.ThrowUtils;
import com.app.project.mapper.HiringRecordMapper;
import com.app.project.model.dto.hiringRecord.HiringRecordAddRequest;
import com.app.project.model.dto.hiringRecord.HiringRecordQueryRequest;
import com.app.project.model.entity.*;
import com.app.project.model.entity.HiringRecord;
import com.app.project.model.enums.JobApplicationStatusEnum;
import com.app.project.model.vo.HiringRecordVO;
import com.app.project.service.HiringRecordService;

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
* @description 针对表【hiring_record】的数据库操作Service实现
* @createDate 2025-04-29 20:14:15
*/
@Service
public class HiringRecordServiceImpl extends ServiceImpl<HiringRecordMapper, HiringRecord>
    implements HiringRecordService {

    @Resource
    private UserService userService;

    @Resource
    private JobPostService jobPostService;

    @Resource
    private JobApplicationService jobApplicationService;

    @Override
    public QueryWrapper<HiringRecord> getQueryWrapper(HiringRecordQueryRequest hiringRecordQueryRequest, User loginUser) {
        QueryWrapper<HiringRecord> queryWrapper = new QueryWrapper<>();
        if (hiringRecordQueryRequest == null) {
            return queryWrapper;
        }
        // 取值
        Long id = hiringRecordQueryRequest.getId();
        String userName = hiringRecordQueryRequest.getUserName();
        Integer status = hiringRecordQueryRequest.getStatus();
        String remark = hiringRecordQueryRequest.getRemark();
        Date hireDate = hiringRecordQueryRequest.getHireDate();

        String sortField = hiringRecordQueryRequest.getSortField();
        String sortOrder = hiringRecordQueryRequest.getSortOrder();

        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(remark), "remark", remark);

        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(hireDate), "hireDate", hireDate);

        // 用户名模糊查询
        if (StringUtils.isNotBlank(userName)) {
            QueryWrapper<User> jobPostQueryWrapper = new QueryWrapper<>();
            jobPostQueryWrapper.like("userName", userName);
            Set<Long> userIds = userService.list(jobPostQueryWrapper).stream().map(User::getId).collect(Collectors.toSet());
            // 用户为空直接查询空值
            if (CollUtil.isEmpty(userIds)) {
                queryWrapper.eq("id", -1);
            }
            queryWrapper.in(CollUtil.isNotEmpty(userIds), "userId", userIds);
        }


        // 企业用户只能获取自己的录用记录
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
    public Page<HiringRecordVO> getHiringRecordVOPage(Page<HiringRecord> hiringRecordPage) {
        List<HiringRecord> hiringRecordList = hiringRecordPage.getRecords();
        Page<HiringRecordVO> hiringRecordVOPage = new Page<>(hiringRecordPage.getCurrent(), hiringRecordPage.getSize(), hiringRecordPage.getTotal());
        if (CollUtil.isEmpty(hiringRecordList)) {
            return hiringRecordVOPage;
        }

        // 1. 批量查询用户（包括用户本人和企业用户）
        Set<Long> userIdSet = hiringRecordList.stream().flatMap(item -> Stream.of(item.getUserId(), item.getEnterPriseId())).collect(Collectors.toSet());

        Map<Long, User> userIdUserMap = userService.listByIds(userIdSet).stream().collect(Collectors.toMap(User::getId, Function.identity()));

        // 2. 批量查询岗位
        Set<Long> jobIdSet = hiringRecordList.stream().map(HiringRecord::getJobPostId).collect(Collectors.toSet());

        Map<Long, JobPost> jobPostIdJobPostMap = jobPostService.listByIds(jobIdSet).stream().collect(Collectors.toMap(JobPost::getId, Function.identity()));

        // 3. 转换为VO
        List<HiringRecordVO> hiringRecordVOList = hiringRecordList.stream().map(item -> {
            HiringRecordVO vo = HiringRecordVO.objToVo(item);

            // 填充用户信息
            User user = userIdUserMap.get(item.getUserId());
            vo.setUser(user != null ? userService.getUserVO(user) : null);

            // 填充企业信息
            User enterprise = userIdUserMap.get(item.getEnterPriseId());
            vo.setEnterprise(enterprise != null ? userService.getUserVO(enterprise) : null);

            // 填充岗位信息
            JobPost jobPost = jobPostIdJobPostMap.get(item.getJobPostId());
            vo.setJobPost(jobPost != null ? jobPostService.getJobPostVO(jobPost) : null);

            return vo;
        }).collect(Collectors.toList());

        hiringRecordVOPage.setRecords(hiringRecordVOList);
        return hiringRecordVOPage;
    }

    @Override
    public Boolean addHiringRecord(HiringRecordAddRequest hiringRecordAddRequest) {

        User loginUser = userService.getLoginUser();
        // 处将实体类和 DTO 进行转换
        HiringRecord hiringRecord = new HiringRecord();
        BeanUtils.copyProperties(hiringRecordAddRequest, hiringRecord);

        // 用户是否存在
        Long hiringUserId = hiringRecordAddRequest.getUserId();
        User hiringUser = userService.getById(hiringUserId);
        ThrowUtils.throwIf( hiringUser == null, ErrorCode.NOT_FOUND_ERROR,"用户不存在");

        // 用户是否面试通过
        QueryWrapper<JobApplication> jobApplicationQueryWrapper = new QueryWrapper<>();
        jobApplicationQueryWrapper.eq("userId", hiringUserId)
                .eq("enterpriseId", loginUser.getId())
                .eq("status", JobApplicationStatusEnum.INTERVIEW_PASSED.getValue());
        JobApplication jobApplication = jobApplicationService.getOne(jobApplicationQueryWrapper);
        ThrowUtils.throwIf(jobApplication == null, ErrorCode.OPERATION_ERROR,"未查询到用户面试通过记录");


        // 是否重复录用
        QueryWrapper<HiringRecord> hiringRecordQueryWrapper = new QueryWrapper<>();
        hiringRecordQueryWrapper.eq("userId", hiringUserId)
                .eq("enterPriseId",loginUser.getId());
        long count = this.count(hiringRecordQueryWrapper);
        ThrowUtils.throwIf(count > 0, ErrorCode.OPERATION_ERROR,"用户已存在");

        //  填充Id
        hiringRecord.setUserId(hiringUserId);
        hiringRecord.setEnterPriseId(loginUser.getId());
        hiringRecord.setJobPostId(jobApplication.getJobId());

        // 转为json字符串，填充文件列表,
        List<Object> fileList = hiringRecordAddRequest.getFileList();
        hiringRecord.setFileList(JSONUtil.toJsonStr(fileList));

        // 写入数据库
        boolean result = this.save(hiringRecord);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }
}




