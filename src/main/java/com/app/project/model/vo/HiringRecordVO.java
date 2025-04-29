package com.app.project.model.vo;

import cn.hutool.json.JSONUtil;
import com.app.project.model.entity.HiringRecord;
import com.app.project.model.entity.JobApplication;
import com.app.project.model.entity.JobPost;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 录用记录视图
 *
 * @author
 * @from
 */
@Data
public class HiringRecordVO implements Serializable {

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
     * 岗位id
     */
    private Long jobPostId;

    /**
     * 岗位信息
     */
    private JobPostVO jobPost;

    /**
     * 入职日期
     */
    private Date hireDate;

    /**
     * 离职日期
     */
    private Date leaveDate;

    /**
     * 附件地址
     */
    private List<Object> fileList;

    /**
     * 0-在职 1-离职
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;



    /**
     * 封装类转对象
     *
     * @param hiringRecordVO
     * @return
     */
    public static HiringRecord voToObj(HiringRecordVO hiringRecordVO) {
        if (hiringRecordVO == null) {
            return null;
        }
        HiringRecord hiringRecord = new HiringRecord();
        BeanUtils.copyProperties(hiringRecordVO, hiringRecord);
        List<Object> fileList = hiringRecordVO.getFileList();
        hiringRecord.setFileList(JSONUtil.toJsonStr(fileList));
        return hiringRecord;
    }

    /**
     * 对象转封装类
     *
     * @param hiringRecord
     * @return
     */
    public static HiringRecordVO objToVo(HiringRecord hiringRecord) {
        if (hiringRecord == null) {
            return null;
        }
        HiringRecordVO hiringRecordVO = new HiringRecordVO();
        BeanUtils.copyProperties(hiringRecord, hiringRecordVO);
        hiringRecordVO.setFileList(JSONUtil.toList(hiringRecord.getFileList(), Object.class));
        return hiringRecordVO;
    }


}
