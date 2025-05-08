package com.app.project.model.vo;

import cn.hutool.json.JSONUtil;
import com.app.project.model.entity.Salary;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 薪酬视图
 *
 * @author
 * @from
 */
@Data
public class SalaryVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 用户信息
     */
    private UserVO user;

    /**
     * 企业Id
     */
    private Long enterPriseId;
    /**
     * 企业信息
     */
    private UserVO enterprise;

    /**
     * date-日结 week-周结 month-月结
     */
    private String salaryType;

    /**
     * 结算周期
     */
    private String periodDate;

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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
