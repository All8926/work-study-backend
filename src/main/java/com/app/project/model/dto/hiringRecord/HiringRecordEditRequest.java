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
 * 编辑录用记录请求
 *
 * @author
 * @from
 */
@Data
public class HiringRecordEditRequest implements Serializable {

    /**
     * id
     */
    @NotNull(message = "id不能为空")
    private Long id;


    /**
     * 附件地址
     */
    private List<Object> fileList;

    /**
     * 0-在职 1-离职
     */
    private Integer status;

    /**
     * 离职时间
     */
    private Date leaveDate;

    /**
     * 备注
     */
    private String remark;



    private static final long serialVersionUID = 1L;
}