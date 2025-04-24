package com.app.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 意见反馈
 * @TableName feedback
 */
@TableName(value ="feedback")
@Data
public class Feedback implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建人
     */
    private Long userId;

    /**
     * 0-待处理  1-已处理 2-不予处理
     */
    private Integer status;

    /**
     * 回复内容
     */
    private String responseText;

    /**
     * 回复人id
     */
    private Long responseUserId;

    /**
     * 回复人姓名
     */
    private String responseUserName;

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