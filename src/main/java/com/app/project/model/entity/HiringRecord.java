package com.app.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName hiring_record
 */
@TableName(value ="hiring_record")
@Data
public class HiringRecord implements Serializable {
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
     * 岗位id
     */
    private Long jobPostId;

    /**
     * 入职日期
     */
    private Date hireDate;

    /**
     * 离职日期
     */
    private Date leaveDate;

    /**
     * 附件地址
     */
    private String fileList;

    /**
     * 0-在职 1-离职
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

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