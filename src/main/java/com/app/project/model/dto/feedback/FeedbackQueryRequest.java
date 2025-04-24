package com.app.project.model.dto.feedback;

import com.app.project.common.PageRequest;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询投诉请求
 *
 * @author
 * @from
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FeedbackQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;


    /**
     * 0-待处理  1-已处理 2-不予处理
     */
    private Integer status;


    private static final long serialVersionUID = 1L;
}