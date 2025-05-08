package com.app.project.model.dto.salary;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建薪酬请求
 *
 * @author
 * @from
 */
@Data
public class SalaryAddRequest implements Serializable {

    /**
     * 用户Id
     */
    @NotNull(message = "用户不能为空")
    private Long userId;

    /**
     * date-日结 week-周结 month-月结
     */
    @NotBlank(message = "结算类型不能为空")
    private String salaryType;

    /**
     * 结算周期
     */
    @NotBlank(message = "结算周期不能为空")
    private String periodDate;

    /**
     * 出勤时长
     */
    @NotBlank(message = "出勤时长不能为空")
    private String workDuration;

    /**
     * 绩效等级：A/B/C等
     */
    private String performance;

    /**
     * 实发工资
     */
    @NotBlank(message = "实发工资不能为空")
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