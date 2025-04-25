package com.app.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 岗位信息
 * @TableName job_post
 */
@TableName(value ="job_post")
@Data
public class JobPost implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建人Id
     */
    private Long userId;

    /**
     * 岗位名称
     */
    private String title;

    /**
     * 岗位描述
     */
    private String description;

    /**
     * 工资
     */
    private String salary;

    /**
     * 任职要求
     */
    private String requirement;

    /**
     * 工作地点
     */
    private String workAddress;

    /**
     * 招聘人数
     */
    private Integer maxCount;

    /**
     * 截止时间
     */
    private Date expirationTime;

    /**
     * 0-待审核 1-已发布 2-已下线 3-审核不通过
     */
    private Integer status;

    /**
     * 拒绝原因
     */
    private String rejectReason;

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