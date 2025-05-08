package com.app.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.app.project.common.ErrorCode;
import com.app.project.constant.CommonConstant;
import com.app.project.constant.UserConstant;
import com.app.project.exception.BusinessException;
import com.app.project.exception.ThrowUtils;
import com.app.project.mapper.AttendanceMapper;
import com.app.project.model.dto.attendance.AttendanceAddRequest;
import com.app.project.model.dto.attendance.AttendanceQueryRequest;
import com.app.project.model.entity.Attendance;
import com.app.project.model.entity.Attendance;
import com.app.project.model.entity.User;
import com.app.project.model.enums.AttendanceStatusEnum;
import com.app.project.model.vo.AttendanceVO;
import com.app.project.service.AttendanceService;

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
 * @description 针对表【attendance(考勤)】的数据库操作Service实现
 * @createDate 2025-05-04 14:16:27
 */
@Service
public class AttendanceServiceImpl extends ServiceImpl<AttendanceMapper, Attendance>
        implements AttendanceService {
    @Resource
    private UserService userService;

    @Override
    public Boolean addAttendance(AttendanceAddRequest attendanceAddRequest) {

        User loginUser = userService.getLoginUser();
        // 处将实体类和 DTO 进行转换
        Attendance attendance = new Attendance();
        BeanUtils.copyProperties(attendanceAddRequest, attendance);

        // 用户是否存在
        Long attendanceUserId = attendanceAddRequest.getUserId();
        User hiringUser = userService.getById(attendanceUserId);
        ThrowUtils.throwIf(hiringUser == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");

        // 状态是否存在
        Integer addRequestStatus = attendanceAddRequest.getStatus();
        if (addRequestStatus != null && !AttendanceStatusEnum.getValues().contains(addRequestStatus)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "状态不存在");
        }

        //  填充Id
        attendance.setUserId(attendanceUserId);
        attendance.setEnterPriseId(loginUser.getId());

        // 写入数据库
        boolean result = this.save(attendance);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;

    }

    @Override
    public QueryWrapper<Attendance> getQueryWrapper(AttendanceQueryRequest attendanceQueryRequest, User loginUser) {
        QueryWrapper<Attendance> queryWrapper = new QueryWrapper<>();
        if (attendanceQueryRequest == null) {
            return queryWrapper;
        }
        // 取值
        Long id = attendanceQueryRequest.getId();
        String userName = attendanceQueryRequest.getUserName();
        Integer status = attendanceQueryRequest.getStatus();
        Date attendanceDate = attendanceQueryRequest.getAttendanceDate();

        String sortField = attendanceQueryRequest.getSortField();
        String sortOrder = attendanceQueryRequest.getSortOrder();

        // 模糊查询

        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(attendanceDate), "attendanceDate", attendanceDate);

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


        // 企业用户只能获取自己员工的考勤记录
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
    public Page<AttendanceVO> getAttendanceVOPage(Page<Attendance> attendancePage) {
        List<Attendance> attendanceList = attendancePage.getRecords();
        Page<AttendanceVO> attendanceVOPage = new Page<>(attendancePage.getCurrent(), attendancePage.getSize(), attendancePage.getTotal());
        if (CollUtil.isEmpty(attendanceList)) {
            return attendanceVOPage;
        }

        // 1. 批量查询用户（包括用户本人和企业用户）
        Set<Long> userIdSet = attendanceList.stream().flatMap(item -> Stream.of(item.getUserId(), item.getEnterPriseId())).collect(Collectors.toSet());

        Map<Long, User> userIdUserMap = userService.listByIds(userIdSet).stream().collect(Collectors.toMap(User::getId, Function.identity()));

        // 2. 转换为VO
        List<AttendanceVO> attendanceVOList = attendanceList.stream().map(item -> {
            AttendanceVO vo = new AttendanceVO();
            BeanUtils.copyProperties(item, vo);

            // 填充用户信息
            User user = userIdUserMap.get(item.getUserId());
            vo.setUser(user != null ? userService.getUserVO(user) : null);

            // 填充企业信息
            User enterprise = userIdUserMap.get(item.getEnterPriseId());
            vo.setEnterprise(enterprise != null ? userService.getUserVO(enterprise) : null);


            return vo;
        }).collect(Collectors.toList());

        attendanceVOPage.setRecords(attendanceVOList);
        return attendanceVOPage;
    }
}




