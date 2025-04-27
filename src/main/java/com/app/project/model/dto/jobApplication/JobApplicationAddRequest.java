package com.app.project.model.dto.jobApplication;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 创建岗位申请请求
 *
 * @author
 * @from
 */
@Data
public class JobApplicationAddRequest implements Serializable {


    /**
     * 岗位ID
     */
    @NotNull(message = "岗位不能为空")
    private Long jobId;

    /**
     * 面试时间
     */
    @NotNull(message = "面试时间不能为空")
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