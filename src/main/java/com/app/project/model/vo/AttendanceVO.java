package com.app.project.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 薪酬视图
 *
 * @author
 * @from
 */
@Data
public class AttendanceVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 用户信息
     */
    private UserVO user;

    /**
     * 企业Id
     */
    private Long enterPriseId;
    /**
     * 企业信息
     */
    private UserVO enterprise;


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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
