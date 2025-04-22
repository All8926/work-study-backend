package com.app.project.model.dto.notice;

import com.app.project.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 查询公告请求
 *
 * @author
 * @from
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NoticeQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    @NotNull
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
     * 0-未发布  1-已发布
     */
    private Integer status;


    private static final long serialVersionUID = 1L;
}