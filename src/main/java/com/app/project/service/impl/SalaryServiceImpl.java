package com.app.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.app.project.common.ErrorCode;
import com.app.project.constant.CommonConstant;
import com.app.project.constant.UserConstant;
import com.app.project.exception.ThrowUtils;
import com.app.project.mapper.SalaryMapper;
import com.app.project.model.dto.salary.SalaryQueryRequest;
import com.app.project.model.dto.salary.SalaryAddRequest;
import com.app.project.model.entity.*;
import com.app.project.model.enums.JobApplicationStatusEnum;
import com.app.project.model.vo.SalaryVO;
import com.app.project.service.SalaryService;

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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
* @author Administrator
* @description 针对表【salary(薪酬)】的数据库操作Service实现
* @createDate 2025-05-03 14:18:40
*/
@Service
public class SalaryServiceImpl extends ServiceImpl<SalaryMapper, Salary>
    implements SalaryService {

    @Resource
    private UserService userService;

    @Override
    public Boolean addSalary(SalaryAddRequest salaryAddRequest) {

        User loginUser = userService.getLoginUser();
        // 处将实体类和 DTO 进行转换
        Salary salary = new Salary();
        BeanUtils.copyProperties(salaryAddRequest, salary);

        // 用户是否存在
        Long salaryUserId = salaryAddRequest.getUserId();
        User hiringUser = userService.getById(salaryUserId);
        ThrowUtils.throwIf( hiringUser == null, ErrorCode.NOT_FOUND_ERROR,"用户不存在");


        //  填充Id
        salary.setUserId(salaryUserId);
        salary.setEnterPriseId(loginUser.getId());

        // 写入数据库
        boolean result = this.save(salary);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
        
    }

    @Override
    public QueryWrapper<Salary> getQueryWrapper(SalaryQueryRequest salaryQueryRequest, User loginUser) {
        QueryWrapper<Salary> queryWrapper = new QueryWrapper<>();
        if (salaryQueryRequest == null) {
            return queryWrapper;
        }
        // 取值
        Long id = salaryQueryRequest.getId();
        String userName = salaryQueryRequest.getUserName();
        Integer issueStatus = salaryQueryRequest.getIssueStatus();
        String salaryAmount = salaryQueryRequest.getSalaryAmount();
        String periodDate = salaryQueryRequest.getPeriodDate();
        String salaryType = salaryQueryRequest.getSalaryType();
        String performance = salaryQueryRequest.getPerformance();

        String sortField = salaryQueryRequest.getSortField();
        String sortOrder = salaryQueryRequest.getSortOrder();

        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(salaryAmount), "salaryAmount", salaryAmount);
        queryWrapper.like(StringUtils.isNotBlank(periodDate), "periodDate", periodDate);
        queryWrapper.like(StringUtils.isNotBlank(performance), "performance", performance);

        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(issueStatus), "issueStatus", issueStatus);
        queryWrapper.eq(ObjectUtils.isNotEmpty(salaryType), "salaryType", salaryType);

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


        // 企业用户只能获取自己员工的薪酬记录
        String userRole = loginUser.getUserRole();
        if (userRole.equals(UserConstant.ENTERPRISE_ROLE)) {
            queryWrapper.eq("enterPriseId", loginUser.getId());
        }

        // 用户只能获取自己
        if (userRole.equals(UserConstant.DEFAULT_ROLE)) {
            queryWrapper.eq("userId", loginUser.getId());
        }


        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_DESC), sortField);
        return queryWrapper;
    }

    @Override
    public Page<SalaryVO> getSalaryVOPage(Page<Salary> salaryPage) {
        List<Salary> salaryList = salaryPage.getRecords();
        Page<SalaryVO> salaryVOPage = new Page<>(salaryPage.getCurrent(), salaryPage.getSize(), salaryPage.getTotal());
        if (CollUtil.isEmpty(salaryList)) {
            return salaryVOPage;
        }

        // 1. 批量查询用户（包括用户本人和企业用户）
        Set<Long> userIdSet = salaryList.stream().flatMap(item -> Stream.of(item.getUserId(), item.getEnterPriseId())).collect(Collectors.toSet());

        Map<Long, User> userIdUserMap = userService.listByIds(userIdSet).stream().collect(Collectors.toMap(User::getId, Function.identity()));

        // 2. 转换为VO
        List<SalaryVO> salaryVOList = salaryList.stream().map(item -> {
            SalaryVO vo = new SalaryVO();
            BeanUtils.copyProperties(item, vo);

            // 填充用户信息
            User user = userIdUserMap.get(item.getUserId());
            vo.setUser(user != null ? userService.getUserVO(user) : null);

            // 填充企业信息
            User enterprise = userIdUserMap.get(item.getEnterPriseId());
            vo.setEnterprise(enterprise != null ? userService.getUserVO(enterprise) : null);


            return vo;
        }).collect(Collectors.toList());

        salaryVOPage.setRecords(salaryVOList);
        return salaryVOPage;
    }

}




