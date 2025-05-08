package com.app.project.model.dto.attendance;

import com.app.project.common.PageRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询薪酬请求
 *
 * @author
 * @from
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AttendanceQueryRequest extends PageRequest implements Serializable {



    /**
     * id
     */
    private Long id;

    /**
     * 用户姓名
     */
    private String userName;


    /**
     * 考勤日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date attendanceDate;


    /**
     * 0-正常 1-迟到 2-早退 3-旷工 4-请假
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}