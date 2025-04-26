package com.app.project.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.json.JSONUtil;
import com.app.project.common.*;
import com.app.project.constant.UserConstant;
import com.app.project.exception.BusinessException;
import com.app.project.exception.ThrowUtils;
import com.app.project.model.dto.jobPost.JobPostAddRequest;
import com.app.project.model.dto.jobPost.JobPostEditRequest;
import com.app.project.model.dto.jobPost.JobPostQueryRequest;
import com.app.project.model.entity.JobPost;
import com.app.project.model.entity.Notice;
import com.app.project.model.entity.User;
import com.app.project.model.enums.AddStatusEnum;
import com.app.project.model.enums.JobPostStatusEnum;
import com.app.project.model.vo.JobPostVO;
import com.app.project.service.JobPostService;
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
import java.util.List;
import java.util.Objects;

import static com.app.project.constant.CommonConstant.REJECTED_STATUS;
import static com.app.project.constant.CommonConstant.RESOLVED_STATUS;

/**
 * 意见反馈接口
 *
 * @author
 * @from
 */
@RestController
@RequestMapping("/jobPost")
@Slf4j
public class JobPostController {

    @Resource
    private JobPostService jobPostService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建意见反馈
     *
     * @param jobPostAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addJobPost(@Valid @RequestBody JobPostAddRequest jobPostAddRequest) {
        ThrowUtils.throwIf(jobPostAddRequest == null, ErrorCode.PARAMS_ERROR);
        // 处将实体类和 DTO 进行转换
        JobPost jobPost = new JobPost();
        BeanUtils.copyProperties(jobPostAddRequest, jobPost);

        //  填充userId
        User loginUser = userService.getLoginUser();
        jobPost.setUserId(loginUser.getId());

        // 写入数据库
        boolean result = jobPostService.save(jobPost);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newJobPostId = jobPost.getId();
        return ResultUtils.success(newJobPostId);
    }

    /**
     * 删除意见反馈
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteJobPost(@Valid @RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        JobPost oldJobPost = jobPostService.getById(id);
        ThrowUtils.throwIf(oldJobPost == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人可删除
        if (!oldJobPost.getUserId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = jobPostService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 审核意见反馈（仅管理员可用）
     *
     * @param auditRequest
     * @return
     */
    @PostMapping("/audit")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> auditJobPost(@Valid @RequestBody AuditRequest auditRequest) {
        if (auditRequest == null || auditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 判断是否存在
        long id = auditRequest.getId();
        JobPost oldJobPost = jobPostService.getById(id);
        ThrowUtils.throwIf(oldJobPost == null, ErrorCode.NOT_FOUND_ERROR);

        // 只能更新待处理状态的
        ThrowUtils.throwIf(!Objects.equals(oldJobPost.getStatus(), JobPostStatusEnum.IN_REVIEW.getValue()), ErrorCode.OPERATION_ERROR, "状态未在审核中");

        // 通过
        if (Objects.equals(auditRequest.getStatus(), RESOLVED_STATUS)) {
            oldJobPost.setStatus(JobPostStatusEnum.HAVE_PUBLISHED.getValue());
        }
        // 拒绝
        if (Objects.equals(auditRequest.getStatus(), REJECTED_STATUS)) {
            oldJobPost.setStatus(JobPostStatusEnum.REJECTED.getValue());
            oldJobPost.setRejectReason(auditRequest.getReason());
        }

        // 操作数据库
        boolean result = jobPostService.updateById(oldJobPost);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    /**
     * 分页获取意见反馈列表（封装类）
     *
     * @param jobPostQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<JobPostVO>> listJobPostVOByPage(@RequestBody JobPostQueryRequest jobPostQueryRequest) {
        long current = jobPostQueryRequest.getCurrent();
        long size = jobPostQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        User loginUser = userService.getLoginUser();
        Page<JobPost> jobPostPage = jobPostService.page(new Page<>(current, size),
                jobPostService.getQueryWrapper(jobPostQueryRequest, loginUser));
        // 获取封装类
        return ResultUtils.success(jobPostService.getJobPostVOPage(jobPostPage));
    }


    /**
     * 编辑意见反馈（给用户使用）
     *
     * @param jobPostEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editJobPost(@Valid @RequestBody JobPostEditRequest jobPostEditRequest, HttpServletRequest request) {
        if (jobPostEditRequest == null || jobPostEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        JobPost jobPost = new JobPost();
        BeanUtils.copyProperties(jobPostEditRequest, jobPost);


        User loginUser = userService.getLoginUser();
        // 判断是否存在
        long id = jobPostEditRequest.getId();
        JobPost oldJobPost = jobPostService.getById(id);
        ThrowUtils.throwIf(oldJobPost == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人可编辑
        if (!oldJobPost.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 修改需要重新审核
        jobPost.setStatus(JobPostStatusEnum.IN_REVIEW.getValue());

        // 操作数据库
        boolean result = jobPostService.updateById(jobPost);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 发布/取消公告
     */
    @PostMapping("/publish")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> publishJobPost(@RequestBody Integer id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        JobPost jobPost = jobPostService.getById(id);
        ThrowUtils.throwIf(jobPost == null, ErrorCode.NOT_FOUND_ERROR);

        JobPost notice = new JobPost();
        BeanUtils.copyProperties(jobPost, notice);

        // 状态取反
        int havePublishedValue = JobPostStatusEnum.HAVE_PUBLISHED.getValue();
        int not_publishedValue = JobPostStatusEnum.NOT_PUBLISHED.getValue();
        notice.setStatus(notice.getStatus() == havePublishedValue ? not_publishedValue : havePublishedValue);

        // 操作数据库
        boolean result = jobPostService.updateById(notice);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    // endregion
}
