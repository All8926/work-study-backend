package com.app.project.model.dto.notice;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 创建公告请求
 *
 * @author
 * @from
 */
@Data
public class NoticeAddRequest implements Serializable {

    /**
     * 标题
     */
    @NotBlank
    private String title;


    /**
     * 内容
     */
    @NotBlank
    private String content;


    private static final long serialVersionUID = 1L;
}