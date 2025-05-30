package com.app.project.model.dto.jobApplication;

import com.app.project.common.PageRequest;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询岗位申请记录请求
 *
 * @author
 * @from
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JobApplicationQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 岗位名称
     */
    private String jobName;


    /**
     * 0-待审核 1-审核拒绝 2-待面试 3-面试不通过 4-面试通过
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}