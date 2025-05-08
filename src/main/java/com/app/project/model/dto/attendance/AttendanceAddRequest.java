package com.app.project.model.dto.attendance;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * 创建薪酬请求
 *
 * @author
 * @from
 */
@Data
public class AttendanceAddRequest implements Serializable {



    /**
     * 用户Id
     */
    @NotNull(message = "用户不能为空")
    private Long userId;

    /**
     * 考勤日期
     */
    @NotNull(message = "考勤日期不能为空")
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