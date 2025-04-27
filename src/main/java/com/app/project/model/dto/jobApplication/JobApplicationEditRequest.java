package com.app.project.model.dto.jobApplication;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 编辑岗位申请请求
 *
 * @author
 * @from
 */
@Data
public class JobApplicationEditRequest implements Serializable {


    /**
     * id
     */
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 面试时间
     */
    private Date interviewTime;

    /**
     * 附件列表
     */
    private List<Object> fileList;


    /**
     * 备注信息
     */
    private String remark;


    private static final long serialVersionUID = 1L;
}