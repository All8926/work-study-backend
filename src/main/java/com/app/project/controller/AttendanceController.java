package com.app.project.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.app.project.common.BaseResponse;
import com.app.project.common.DeleteRequest;
import com.app.project.common.ErrorCode;
import com.app.project.common.ResultUtils;
import com.app.project.constant.UserConstant;
import com.app.project.exception.BusinessException;
import com.app.project.exception.ThrowUtils;
import com.app.project.model.dto.attendance.AttendanceAddRequest;
import com.app.project.model.dto.attendance.AttendanceQueryRequest;
import com.app.project.model.dto.attendance.AttendanceUpdateRequest;
import com.app.project.model.entity.Attendance;
import com.app.project.model.entity.User;
import com.app.project.model.vo.AttendanceVO;
import com.app.project.service.AttendanceService;
import com.app.project.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 考勤接口
 *
 * @author
 * @from
 */
@RestController
@RequestMapping("/attendance")
@Slf4j
public class AttendanceController {

    @Resource
    private AttendanceService attendanceService;


    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建考勤
     *
     * @param attendanceAddRequest
     * @return
     */
    @PostMapping("/add")
    @SaCheckRole(UserConstant.ENTERPRISE_ROLE)
    public BaseResponse<Boolean> addAttendance(@Valid @RequestBody AttendanceAddRequest attendanceAddRequest) {
        ThrowUtils.throwIf(attendanceAddRequest == null, ErrorCode.PARAMS_ERROR);

        Boolean result = attendanceService.addAttendance(attendanceAddRequest);
        return ResultUtils.success(result);
    }

    /**
     * 删除考勤
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    @SaCheckRole(UserConstant.ENTERPRISE_ROLE)
    public BaseResponse<Boolean> deleteAttendance(@Valid @RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        Attendance oldAttendance = attendanceService.getById(id);
        ThrowUtils.throwIf(oldAttendance == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人可删除
        if (!oldAttendance.getEnterPriseId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = attendanceService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    /**
     * 分页获取考勤列表（封装类）
     *
     * @param attendanceQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<AttendanceVO>> listAttendanceVOByPage(@RequestBody AttendanceQueryRequest attendanceQueryRequest) {
        long current = attendanceQueryRequest.getCurrent();
        long size = attendanceQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        User loginUser = userService.getLoginUser();
        Page<Attendance> attendancePage = attendanceService.page(new Page<>(current, size),
                attendanceService.getQueryWrapper(attendanceQueryRequest, loginUser));
        // 获取封装类
        return ResultUtils.success(attendanceService.getAttendanceVOPage(attendancePage));
    }


    /**
     * 编辑考勤
     *
     * @param attendanceUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @SaCheckRole(UserConstant.ENTERPRISE_ROLE)
    public BaseResponse<Boolean> editAttendance(@Valid @RequestBody AttendanceUpdateRequest attendanceUpdateRequest ) {
        if (attendanceUpdateRequest == null || attendanceUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Attendance attendance = new Attendance();
        BeanUtils.copyProperties(attendanceUpdateRequest, attendance);

        User loginUser = userService.getLoginUser();
        // 判断是否存在
        long id = attendanceUpdateRequest.getId();
        Attendance oldAttendance = attendanceService.getById(id);
        ThrowUtils.throwIf(oldAttendance == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人可编辑
        if (!oldAttendance.getEnterPriseId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 操作数据库
        boolean result = attendanceService.updateById(attendance);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }




    // endregion
}
