package com.app.project.service;


import com.app.project.model.dto.notice.NoticeQueryRequest;
import com.app.project.model.entity.Notice;
import com.app.project.model.entity.User;
import com.app.project.model.vo.NoticeVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author Administrator
* @description 针对表【notice(公告)】的数据库操作Service
* @createDate 2025-04-18 20:56:25
*/
public interface NoticeService extends IService<Notice> {

    /**
     * 获取查询条件
     * @param noticeQueryRequest
     * @param loginUser
     * @return
     */
    QueryWrapper<Notice> getQueryWrapper(NoticeQueryRequest noticeQueryRequest, User loginUser);

    /**
     * 分页获取公告列表封装
     * @param noticePage
     * @return
     */
    Page<NoticeVO> getNoticeVOPage(Page<Notice> noticePage);
}
