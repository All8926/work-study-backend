package com.app.project.model.dto.feedback;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 更新投诉请求
 *
 */
@Data
public class FeedbackUpdateRequest implements Serializable {

    /**
     * id
     */
    @NotNull(message = "id不能为空")
    private Long id;


    /**
     * 0-待处理  1-已处理 2-不予处理
     */
    private Integer status;

    /**
     * 回复内容
     */
    private String responseText;



    private static final long serialVersionUID = 1L;
}