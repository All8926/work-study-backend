package com.app.project.model.dto.feedback;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建投诉请求
 *
 * @author
 * @from
 */
@Data
public class FeedbackAddRequest implements Serializable {

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 图片
     */
    private List<String> imageList;

    private static final long serialVersionUID = 1L;
}