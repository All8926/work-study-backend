package com.app.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 岗位申请
 * @TableName job_application
 */
@TableName(value ="job_application")
@Data
public class JobApplication implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 岗位ID
     */
    private Long jobId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 企业用户id
     */
    private Long enterpriseId;


    /**
     * 面试时间
     */
    private Date interviewTime;

    /**
     * 0-待审核 1-已通过 2-已拒绝
     */
    private Integer status;

    /**
     * 附件列表
     */
    private String fileList;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 审核说明
     */
    private String auditExplain;

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