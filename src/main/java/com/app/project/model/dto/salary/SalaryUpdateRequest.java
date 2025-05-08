package com.app.project.model.dto.salary;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 更新薪酬请求
 *
 */
@Data
public class SalaryUpdateRequest implements Serializable {


    /**
     * id
     */
    @NotNull(message = "id不能为空")
    private Long id;


    /**
     * 出勤时长
     */
    private String workDuration;

    /**
     * 绩效等级：A/B/C等
     */
    private String performance;

    /**
     * 实发工资
     */
    private String salaryAmount;

    /**
     * 0-未发 1-已发
     */
    private Integer issueStatus;

    /**
     * 发放时间
     */
    private Date issueTime;


    private static final long serialVersionUID = 1L;
}