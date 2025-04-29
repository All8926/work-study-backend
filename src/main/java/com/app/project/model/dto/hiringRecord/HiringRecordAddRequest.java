package com.app.project.model.dto.hiringRecord;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建录用记录请求
 *
 * @author
 * @from
 */
@Data
public class HiringRecordAddRequest implements Serializable {


    /**
     * 用户Id
     */
    @NotNull(message = "用户不能为空")
    private Long userId;


    /**
     * 入职日期
     */
    private Date hireDate;


    /**
     * 附件地址
     */
    private List<Object> fileList;


    /**
     * 备注
     */
    private String remark;


    private static final long serialVersionUID = 1L;
}