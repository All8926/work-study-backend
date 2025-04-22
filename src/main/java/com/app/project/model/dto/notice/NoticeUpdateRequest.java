package com.app.project.model.dto.notice;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 更新公告请求
 *
 */
@Data
public class NoticeUpdateRequest implements Serializable {

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
     * 内容
     */
    private String content;


    private static final long serialVersionUID = 1L;
}