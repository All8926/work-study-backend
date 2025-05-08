package com.app.project.service;


import com.app.project.model.dto.attendance.AttendanceAddRequest;
import com.app.project.model.dto.attendance.AttendanceQueryRequest;
import com.app.project.model.entity.Attendance;
import com.app.project.model.entity.Attendance;
import com.app.project.model.entity.User;
import com.app.project.model.vo.AttendanceVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【attendance(考勤)】的数据库操作Service
* @createDate 2025-05-04 14:16:27
*/
public interface AttendanceService extends IService<Attendance> {
    /**
     * @description 新增
     * @author luobin YL586246
     * @date 2025/5/3 15:01
     */
    Boolean addAttendance(AttendanceAddRequest attendanceAddRequest);

    /**
     * @description 获取查询条件
     * @author luobin YL586246
     * @date 2025/4/29 20:43
     */
    QueryWrapper<Attendance> getQueryWrapper(AttendanceQueryRequest attendanceQueryRequest, User loginUser);

    /**
     * @description 获取分页数据
     * @author luobin YL586246
     * @date 2025/4/29 20:43
     */
    Page<AttendanceVO> getAttendanceVOPage(Page<Attendance> hiringRecordPage);
}
