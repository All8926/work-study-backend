package com.app.project.model.dto.salary;

import com.app.project.common.PageRequest;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 查询薪酬请求
 *
 * @author
 * @from
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SalaryQueryRequest extends PageRequest implements Serializable {


    /**
     * id
     */
    private Long id;

    /**
     * 用户姓名
     */
    private String userName;


    /**
     * date-日结 week-周结 month-月结
     */
    private String salaryType;

    /**
     * 结算周期
     */
    private String periodDate;


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




    private static final long serialVersionUID = 1L;
}