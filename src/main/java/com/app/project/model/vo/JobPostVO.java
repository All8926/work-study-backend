package com.app.project.model.vo;

import cn.hutool.json.JSONUtil;
import com.app.project.model.entity.Feedback;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 岗位视图
 *
 * @author
 * @from
 */
@Data
public class JobPostVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 创建人Id
     */
    private Long userId;

    /**
     * 创建人信息
     */
    private UserVO user;

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

    /**
     * 0-待审核 1-已发布 2-已下线 3-审核不通过
     */
    private Integer status;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;



}
