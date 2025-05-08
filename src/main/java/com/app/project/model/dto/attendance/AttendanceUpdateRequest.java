package com.app.project.model.dto.attendance;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 更新薪酬请求
 *
 */
@Data
public class AttendanceUpdateRequest implements Serializable {


    /**
     * id
     */
    private Long id;

    /**
     * 考勤日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date attendanceDate;

    /**
     * 上班打卡时间
     */
    private String checkInTime;

    /**
     * 下班打卡时间
     */
    private String checkOutTime;

    /**
     * 工作时长
     */
    private String workDuration;

    /**
     * 0-正常 1-迟到 2-早退 3-旷工 4-请假
     */
    private Integer status;



    private static final long serialVersionUID = 1L;
}