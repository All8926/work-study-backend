package com.app.project.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.json.JSONUtil;
import com.app.project.common.BaseResponse;
import com.app.project.common.DeleteRequest;
import com.app.project.common.ErrorCode;
import com.app.project.common.ResultUtils;
import com.app.project.constant.UserConstant;
import com.app.project.exception.BusinessException;
import com.app.project.exception.ThrowUtils;
import com.app.project.model.dto.feedback.FeedbackAddRequest;
import com.app.project.model.dto.feedback.FeedbackEditRequest;
import com.app.project.model.dto.feedback.FeedbackQueryRequest;
import com.app.project.model.dto.feedback.FeedbackUpdateRequest;
import com.app.project.model.entity.Feedback;
import com.app.project.model.entity.User;
import com.app.project.model.enums.AddStatusEnum;
import com.app.project.model.vo.FeedbackVO;
import com.app.project.service.FeedbackService;
import com.app.project.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * 意见反馈接口
 *
 * @author
 * @from
 */
@RestController
@RequestMapping("/feedback")
@Slf4j
public class FeedbackController {

    @Resource
    private FeedbackService feedbackService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建意见反馈
     *
     * @param feedbackAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addFeedback(@Valid @RequestBody FeedbackAddRequest feedbackAddRequest) {
        ThrowUtils.throwIf(feedbackAddRequest == null, ErrorCode.PARAMS_ERROR);
        // 处将实体类和 DTO 进行转换
        Feedback feedback = new Feedback();
        BeanUtils.copyProperties(feedbackAddRequest, feedback);

        //  填充userId
        User loginUser = userService.getLoginUser();
        feedback.setUserId(loginUser.getId());

        // 写入数据库
        boolean result = feedbackService.save(feedback);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newFeedbackId = feedback.getId();
        return ResultUtils.success(newFeedbackId);
    }

    /**
     * 删除意见反馈
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteFeedback(@Valid @RequestBody DeleteRequest deleteRequest ) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        Feedback oldFeedback = feedbackService.getById(id);
        ThrowUtils.throwIf(oldFeedback == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人可删除
        if (!oldFeedback.getUserId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = feedbackService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新意见反馈（仅管理员可用）
     *
     * @param feedbackUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateFeedback(@Valid @RequestBody FeedbackUpdateRequest feedbackUpdateRequest) {
        if (feedbackUpdateRequest == null || feedbackUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Feedback feedback = new Feedback();
        BeanUtils.copyProperties(feedbackUpdateRequest, feedback);

        // 判断是否存在
        long id = feedbackUpdateRequest.getId();
        Feedback oldFeedback = feedbackService.getById(id);
        ThrowUtils.throwIf(oldFeedback == null, ErrorCode.NOT_FOUND_ERROR);

        // 只能更新待处理状态的
        ThrowUtils.throwIf(!Objects.equals(oldFeedback.getStatus(), AddStatusEnum.IN_REVIEW.getValue()), ErrorCode.OPERATION_ERROR,"不可重复处理");

        // 设置操作人信息
        User user = userService.getLoginUser();
        feedback.setResponseUserId(user.getId());
        feedback.setResponseUserName(user.getNickName());

        // 操作数据库
        boolean result = feedbackService.updateById(feedback);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    /**
     * 分页获取意见反馈列表（封装类）
     *
     * @param feedbackQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<FeedbackVO>> listFeedbackVOByPage(@RequestBody FeedbackQueryRequest feedbackQueryRequest ) {
        long current = feedbackQueryRequest.getCurrent();
        long size = feedbackQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
          User loginUser = userService.getLoginUser();
        Page<Feedback> feedbackPage = feedbackService.page(new Page<>(current, size),
                feedbackService.getQueryWrapper(feedbackQueryRequest,loginUser));
        // 获取封装类
        return ResultUtils.success(feedbackService.getFeedbackVOPage(feedbackPage));
    }


    /**
     * 编辑意见反馈（给用户使用）
     *
     * @param feedbackEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editFeedback(@Valid @RequestBody FeedbackEditRequest feedbackEditRequest, HttpServletRequest request) {
        if (feedbackEditRequest == null || feedbackEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Feedback feedback = new Feedback();
        BeanUtils.copyProperties(feedbackEditRequest, feedback);

        User loginUser = userService.getLoginUser();
        // 判断是否存在
        long id = feedbackEditRequest.getId();
        Feedback oldFeedback = feedbackService.getById(id);
        ThrowUtils.throwIf(oldFeedback == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人可编辑
        if (!oldFeedback.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 只能编辑待处理的
        if (!Objects.equals(oldFeedback.getStatus(), AddStatusEnum.IN_REVIEW.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "已处理，不可编辑");
        }

        // 操作数据库
        boolean result = feedbackService.updateById(feedback);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}
