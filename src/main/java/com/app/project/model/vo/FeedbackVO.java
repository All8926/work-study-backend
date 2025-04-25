package com.app.project.model.vo;

import cn.hutool.json.JSONUtil;
import com.app.project.model.entity.Feedback;
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
public class FeedbackVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;


    /**
     * 图片列表
     */
    private List<String> imageList;

    /**
     * 创建人
     */
    private Long userId;

    /**
     * 创建人信息
     */
    private UserVO user;

    /**
     * 0-待处理  1-已处理 2-不予处理
     */
    private Integer status;

    /**
     * 回复内容
     */
    private String responseText;

    /**
     * 回复人id
     */
    private Long responseUserId;

    /**
     * 回复人姓名
     */
    private String responseUserName;

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
     * @param feedbackVO
     * @return
     */
    public static Feedback voToObj(FeedbackVO feedbackVO) {
        if (feedbackVO == null) {
            return null;
        }
        Feedback feedback = new Feedback();
        BeanUtils.copyProperties(feedbackVO, feedback);
        List<String> tagList = feedbackVO.getImageList();
        feedback.setImage(JSONUtil.toJsonStr(tagList));
        return feedback;
    }

    /**
     * 对象转封装类
     *
     * @param feedback
     * @return
     */
    public static FeedbackVO objToVo(Feedback feedback) {
        if (feedback == null) {
            return null;
        }
        FeedbackVO feedbackVO = new FeedbackVO();
        BeanUtils.copyProperties(feedback, feedbackVO);
        feedbackVO.setImageList(JSONUtil.toList(feedback.getImage(), String.class));
        return feedbackVO;
    }


}
