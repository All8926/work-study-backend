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
 * 查询岗位申请请求
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
     * 0-待审核 1-已通过 2-已拒绝
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}