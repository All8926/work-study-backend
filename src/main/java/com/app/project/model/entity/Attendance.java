package com.app.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 考勤
 * @TableName attendance
 */
@TableName(value ="attendance")
@Data
public class Attendance implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 企业Id
     */
    private Long enterPriseId;

    /**
     * 考勤日期
     */
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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}