package com.app.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.app.project.constant.CommonConstant;
import com.app.project.constant.UserConstant;
import com.app.project.mapper.FeedbackMapper;
import com.app.project.model.dto.feedback.FeedbackQueryRequest;
import com.app.project.model.entity.Feedback;
import com.app.project.model.entity.User;
import com.app.project.model.vo.FeedbackVO;
import com.app.project.service.FeedbackService;

import com.app.project.service.UserService;
import com.app.project.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【feedback(意见反馈)】的数据库操作Service实现
* @createDate 2025-04-24 16:18:51
*/
@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback>
    implements FeedbackService {

    @Resource
    private UserService userService;

    @Override
    public QueryWrapper<Feedback> getQueryWrapper(FeedbackQueryRequest feedbackQueryRequest, User loginUser) {
        QueryWrapper<Feedback> queryWrapper = new QueryWrapper<>();
        if (feedbackQueryRequest == null) {
            return queryWrapper;
        }
        // 取值
        Long id = feedbackQueryRequest.getId();
        String title = feedbackQueryRequest.getTitle();
        Integer status = feedbackQueryRequest.getStatus();

        String sortField = feedbackQueryRequest.getSortField();
        String sortOrder = feedbackQueryRequest.getSortOrder();

        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);

        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);


        // 非管理员只能获取自己的
        String userRole = loginUser.getUserRole();
        if (!userRole.equals(UserConstant.ADMIN_ROLE)) {
            queryWrapper.eq("userId", loginUser.getId());
        }

        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_DESC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<FeedbackVO> getFeedbackVOPage(Page<Feedback> feedbackPage) {
        List<Feedback> feedbackList = feedbackPage.getRecords();
        Page<FeedbackVO> feedbackVOPage = new Page<>(feedbackPage.getCurrent(), feedbackPage.getSize(), feedbackPage.getTotal());
        if (CollUtil.isEmpty(feedbackList)) {
            return feedbackVOPage;
        }
        // Feedback => FeedbackVO
        List<FeedbackVO> feedbackVOList = feedbackList.stream().map(FeedbackVO::objToVo).collect(Collectors.toList());

        // 1. 关联查询用户信息
        Set<Long> userIdSet = feedbackList.stream().map(Feedback::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        feedbackVOList.forEach(feedbackVO -> {
            Long userId = feedbackVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            feedbackVO.setUser(userService.getUserVO(user));
        });
        // endregion

        feedbackVOPage.setRecords(feedbackVOList);
        return feedbackVOPage;
    }
}




