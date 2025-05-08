package com.app.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 薪酬
 * @TableName salary
 */
@TableName(value ="salary")
@Data
public class Salary implements Serializable {
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
     * date-日结 week-周结 month-月结
     */
    private String salaryType;

    /**
     * 结算周期
     */
    private String periodDate;

    /**
     * 出勤时长
     */
    private String workDuration;

    /**
     * 绩效等级：A/B/C等
     */
    private String performance;

    /**
     * 实发工资
     */
    private String salaryAmount;

    /**
     * 0-未发 1-已发
     */
    private Integer issueStatus;

    /**
     * 发放时间
     */
    private Date issueTime;

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