package com.app.project.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.app.project.common.BaseResponse;
import com.app.project.common.DeleteRequest;
import com.app.project.common.ErrorCode;
import com.app.project.common.ResultUtils;
import com.app.project.constant.UserConstant;
import com.app.project.exception.BusinessException;
import com.app.project.exception.ThrowUtils;
import com.app.project.model.dto.notice.NoticeAddRequest;
import com.app.project.model.dto.notice.NoticeQueryRequest;
import com.app.project.model.dto.notice.NoticeUpdateRequest;
import com.app.project.model.entity.Notice;
import com.app.project.model.entity.User;
import com.app.project.model.vo.NoticeVO;
import com.app.project.service.NoticeService;
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
import java.util.Date;

/**
 * 公告接口
 *
 * @author
 * @from
 */
@RestController
@RequestMapping("/notice")
@Slf4j
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建公告
     *
     * @param noticeAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addNotice(@Valid @RequestBody NoticeAddRequest noticeAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(noticeAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeAddRequest, notice);

        // todo 填充默认值
        User loginUser = userService.getLoginUser();
        notice.setUserId(loginUser.getId());

        // 写入数据库
        boolean result = noticeService.save(notice);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newNoticeId = notice.getId();
        return ResultUtils.success(newNoticeId);
    }

    /**
     * 删除公告
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteNotice(@Valid @RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        Notice oldNotice = noticeService.getById(id);
        ThrowUtils.throwIf(oldNotice == null, ErrorCode.NOT_FOUND_ERROR);

        // 操作数据库
        boolean result = noticeService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新公告（仅管理员可用）
     *
     * @param noticeUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateNotice(@Valid @RequestBody NoticeUpdateRequest noticeUpdateRequest) {
        if (noticeUpdateRequest == null || noticeUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeUpdateRequest, notice);

        // 判断是否存在
        long id = noticeUpdateRequest.getId();
        Notice oldNotice = noticeService.getById(id);
        ThrowUtils.throwIf(oldNotice == null, ErrorCode.NOT_FOUND_ERROR);

        // 操作数据库
        boolean result = noticeService.updateById(notice);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    /**
     * 分页获取公告列表（封装类）
     *
     * @param noticeQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<NoticeVO>> listNoticeVOByPage(@RequestBody NoticeQueryRequest noticeQueryRequest ) {
        long current = noticeQueryRequest.getCurrent();
        long size = noticeQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        User loginUser = userService.getLoginUser();
        Page<Notice> noticePage = noticeService.page(new Page<>(current, size),
                noticeService.getQueryWrapper(noticeQueryRequest, loginUser));
        // 获取封装类
        return ResultUtils.success(noticeService.getNoticeVOPage(noticePage ));
    }

    /**
     * 发布/取消公告
     */
    @PostMapping("/publish")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> publishNotice(@RequestBody Integer id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        Notice oldNotice = noticeService.getById(id);
        ThrowUtils.throwIf(oldNotice == null, ErrorCode.NOT_FOUND_ERROR);

        Notice notice = new Notice();
        BeanUtils.copyProperties(oldNotice, notice);

        // 状态取反
        notice.setStatus(notice.getStatus() == 0 ? 1 : 0);
        // 设置发布时间
        if (notice.getStatus() == 1) {
            notice.setPublishTime(new Date());
        }

        // 操作数据库
        boolean result = noticeService.updateById(notice);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    // endregion
}
