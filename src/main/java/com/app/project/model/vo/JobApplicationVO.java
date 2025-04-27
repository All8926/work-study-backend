package com.app.project.model.vo;

import cn.hutool.json.JSONUtil;
import com.app.project.model.entity.Feedback;
import com.app.project.model.entity.JobApplication;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 投诉视图
 *
 * @author
 * @from
 */
@Data
public class JobApplicationVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 岗位ID
     */
    private Long jobId;

    /**
     * 岗位信息
     */
    private JobPostVO job;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户信息
     */
    private UserVO user;

    /**
     * 企业ID
     */
    private Long enterpriseId;

    /**
     * 企业信息
     */
    private UserVO enterprise;

    /**
     * 面试时间
     */
    private Date interviewTime;

    /**
     * 0-待审核 1-已通过 2-已拒绝
     */
    private Integer status;

    /**
     * 附件列表
     */
    private List<Object> fileList;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 审核说明
     */
    private String auditExplain;

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
     * @param jobApplicationVO
     * @return
     */
    public static JobApplication voToObj(JobApplicationVO jobApplicationVO) {
        if (jobApplicationVO == null) {
            return null;
        }
        JobApplication jobApplication = new JobApplication();
        BeanUtils.copyProperties(jobApplicationVO, jobApplication);
        List<Object> fileList = jobApplicationVO.getFileList();
        jobApplication.setFileList(JSONUtil.toJsonStr(fileList));
        return jobApplication;
    }

    /**
     * 对象转封装类
     *
     * @param jobApplication
     * @return
     */
    public static JobApplicationVO objToVo(JobApplication jobApplication) {
        if (jobApplication == null) {
            return null;
        }
        JobApplicationVO feedbackVO = new JobApplicationVO();
        BeanUtils.copyProperties(jobApplication, feedbackVO);
        feedbackVO.setFileList(JSONUtil.toList(jobApplication.getFileList(), Object.class));
        return feedbackVO;
    }


}
