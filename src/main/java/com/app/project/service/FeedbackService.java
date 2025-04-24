package com.app.project.service;


import com.app.project.model.dto.feedback.FeedbackQueryRequest;
import com.app.project.model.entity.Feedback;
import com.app.project.model.entity.User;
import com.app.project.model.vo.FeedbackVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【feedback(意见反馈)】的数据库操作Service
* @createDate 2025-04-24 16:18:51
*/
public interface FeedbackService extends IService<Feedback> {

    /**
     * 获取查询条件
     * @param feedbackQueryRequest
     * @param loginUser
     * @return
     */
    QueryWrapper<Feedback> getQueryWrapper(FeedbackQueryRequest feedbackQueryRequest, User loginUser);

    /**
     * 获取分页数据
     * @param feedbackPage
     * @return
     */
    Page<FeedbackVO> getFeedbackVOPage(Page<Feedback> feedbackPage);
}
