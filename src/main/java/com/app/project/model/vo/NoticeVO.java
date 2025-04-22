package com.app.project.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 公告视图
 *
 * @author
 * @from
 */
@Data
public class NoticeVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 内容
     */
    private String content;

    /**
     * 0-未发布  1-已发布
     */
    private Integer status;

    /**
     * 创建人
     */
    private Long userId;

    /**
     * 创建人信息
     */
    private UserVO user;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
