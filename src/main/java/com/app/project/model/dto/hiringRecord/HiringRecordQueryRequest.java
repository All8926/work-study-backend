package com.app.project.model.dto.hiringRecord;

import com.app.project.common.PageRequest;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询录用记录请求
 *
 * @author
 * @from
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class HiringRecordQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户名字
     */
    private String userName;


    /**
     * 入职日期
     */
    private Date hireDate;


    /**
     * 0-在职 1-离职
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;


    private static final long serialVersionUID = 1L;
}