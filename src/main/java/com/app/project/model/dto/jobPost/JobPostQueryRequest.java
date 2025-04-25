package com.app.project.model.dto.jobPost;

import com.app.project.common.PageRequest;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询投诉请求
 *
 * @author
 * @from
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JobPostQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
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
     * 工作地点
     */
    private String workAddress;

    /**
     * 0-待审核 1-已发布 2-已下线
     */
    private Integer status;


    private static final long serialVersionUID = 1L;
}