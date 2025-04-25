package com.app.project.model.dto.jobPost;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 编辑投诉请求
 *
 * @author
 * @from
 */
@Data
public class JobPostEditRequest implements Serializable {
    /**
     * id
     */
    @NotNull(message = "id不能为空")
    private Long id;


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


    private static final long serialVersionUID = 1L;
}