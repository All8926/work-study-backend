package com.app.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.app.project.constant.CommonConstant;
import com.app.project.constant.UserConstant;
import com.app.project.mapper.NoticeMapper;
import com.app.project.model.dto.notice.NoticeQueryRequest;
import com.app.project.model.entity.Notice;
import com.app.project.model.entity.User;
import com.app.project.model.vo.NoticeVO;
import com.app.project.service.NoticeService;
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
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【notice(公告)】的数据库操作Service实现
* @createDate 2025-04-18 20:56:25
*/
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice>
    implements NoticeService {

    @Resource
    private UserService userService;

    @Override
    public QueryWrapper<Notice> getQueryWrapper(NoticeQueryRequest noticeQueryRequest, User loginUser) {

        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        if (noticeQueryRequest == null) {
            return queryWrapper;
        }
        // 取值
        Long id = noticeQueryRequest.getId();
        String title = noticeQueryRequest.getTitle();
        Date publishTime = noticeQueryRequest.getPublishTime(); 
        Integer status = noticeQueryRequest.getStatus();

        String sortField = noticeQueryRequest.getSortField();
        String sortOrder = noticeQueryRequest.getSortOrder();

        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title); 
        
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);

        // 小于等于来访时间
        queryWrapper.le(ObjectUtils.isNotEmpty(publishTime), "publishTime", publishTime);


        // 非管理员只能获取已发布的公告
        String userRole = loginUser.getUserRole();
        if (!userRole.equals(UserConstant.ADMIN_ROLE)) {
            queryWrapper.eq("status", 1);
        }

        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<NoticeVO> getNoticeVOPage(Page<Notice> noticePage) {
        List<Notice> noticeList = noticePage.getRecords();
        Page<NoticeVO> noticeVOPage = new Page<>(noticePage.getCurrent(), noticePage.getSize(), noticePage.getTotal());
        if (CollUtil.isEmpty(noticeList)) {
            return noticeVOPage;
        }
        // Notice => NoticeVO
        List<NoticeVO> noticeVOList = noticeList.stream().map(notice -> {
            NoticeVO noticeVO = new NoticeVO();
            BeanUtils.copyProperties(notice, noticeVO);
            return noticeVO;
        }).collect(Collectors.toList());

        // 1. 关联查询用户信息
        Set<Long> userIdSet = noticeList.stream().map(Notice::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        noticeVOList.forEach(noticeVO -> {
            Long userId = noticeVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            noticeVO.setUser(userService.getUserVO(user));
        });
        // endregion

        noticeVOPage.setRecords(noticeVOList);
        return noticeVOPage;
    }
}




