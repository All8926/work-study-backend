package com.app.project.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.json.JSONUtil;
import com.app.project.common.*;
import com.app.project.constant.UserConstant;
import com.app.project.exception.BusinessException;
import com.app.project.exception.ThrowUtils;
import com.app.project.model.dto.jobApplication.JobApplicationAddRequest;
import com.app.project.model.dto.jobApplication.JobApplicationEditRequest;
import com.app.project.model.dto.jobApplication.JobApplicationQueryRequest;
import com.app.project.model.entity.JobApplication;
import com.app.project.model.entity.JobPost;
import com.app.project.model.entity.User;
import com.app.project.model.enums.AddStatusEnum;
import com.app.project.model.enums.JobApplicationStatusEnum;
import com.app.project.model.vo.JobApplicationVO;
import com.app.project.model.vo.UserVO;
import com.app.project.service.JobApplicationService;
import com.app.project.service.JobPostService;
import com.app.project.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

import static com.app.project.constant.CommonConstant.REJECTED_STATUS;
import static com.app.project.constant.CommonConstant.RESOLVED_STATUS;

/**
 * 岗位申请接口
 *
 * @author
 * @from
 */
@RestController
@RequestMapping("/jobApplication")
@Slf4j
public class JobApplicationController {

    @Resource
    private JobApplicationService jobApplicationService;

    @Resource
    private JobPostService jobPostService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建岗位申请
     *
     * @param jobApplicationAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addJobApplication(@Valid @RequestBody JobApplicationAddRequest jobApplicationAddRequest) {
        ThrowUtils.throwIf(jobApplicationAddRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser();
        // 处将实体类和 DTO 进行转换
        JobApplication jobApplication = new JobApplication();
        BeanUtils.copyProperties(jobApplicationAddRequest, jobApplication);

        // 岗位是否存在
        Long jobId = jobApplicationAddRequest.getJobId();
        JobPost jobPost = jobPostService.getById(jobId);
        ThrowUtils.throwIf(jobPost == null, ErrorCode.NOT_FOUND_ERROR,"岗位不存在");

        // 是否重复申请
        QueryWrapper<JobApplication> jobApplicationQueryWrapper = new QueryWrapper<>();
        jobApplicationQueryWrapper.eq("jobId", jobId)
                .eq("userId", loginUser.getId());
        long count = jobApplicationService.count(jobApplicationQueryWrapper);
        ThrowUtils.throwIf(count > 0, ErrorCode.OPERATION_ERROR,"您已申请过该岗位，请勿重复申请");

        //  填充userId
        jobApplication.setUserId(loginUser.getId());
        jobApplication.setEnterpriseId(jobPost.getUserId());

        // 转为json字符串，填充文件列表,
        List<Object> fileList = jobApplicationAddRequest.getFileList();
        jobApplication.setFileList(JSONUtil.toJsonStr(fileList));

        // 写入数据库
        boolean result = jobApplicationService.save(jobApplication);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newJobApplicationId = jobApplication.getId();
        return ResultUtils.success(newJobApplicationId);
    }

    /**
     * 删除岗位申请
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteJobApplication(@Valid @RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        JobApplication oldJobApplication = jobApplicationService.getById(id);
        ThrowUtils.throwIf(oldJobApplication == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人可删除
        if (!oldJobApplication.getUserId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = jobApplicationService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 审核岗位申请（仅管理员可用）
     *
     * @param auditRequest
     * @return
     */
    @PostMapping("/audit")
    @SaCheckRole(UserConstant.ENTERPRISE_ROLE)
    public BaseResponse<Boolean> auditJobApplication(@Valid @RequestBody AuditRequest auditRequest) {
        if (auditRequest == null || auditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 1.判断申请记录是否存在
        long id = auditRequest.getId();
        JobApplication oldJobApplication = jobApplicationService.getById(id);
        ThrowUtils.throwIf(oldJobApplication == null, ErrorCode.NOT_FOUND_ERROR);

         Integer status = oldJobApplication.getStatus();
        // 2.审核
        if(Objects.equals(status, JobApplicationStatusEnum.IN_REVIEW.getValue())){
            // 面试申请审核
            if(auditRequest.getStatus() == RESOLVED_STATUS){
                oldJobApplication.setStatus(JobApplicationStatusEnum.IN_INTERVIEW.getValue());
            }
            if(auditRequest.getStatus() == REJECTED_STATUS){
                oldJobApplication.setStatus(JobApplicationStatusEnum.REJECTED.getValue());
            }
        }else if(Objects.equals(status, JobApplicationStatusEnum.IN_INTERVIEW.getValue())){
            // 面试结果审核
            if(auditRequest.getStatus() == RESOLVED_STATUS){
                oldJobApplication.setStatus(JobApplicationStatusEnum.INTERVIEW_PASSED.getValue());
            }
            if(auditRequest.getStatus() == REJECTED_STATUS){
                oldJobApplication.setStatus(JobApplicationStatusEnum.INTERVIEW_FAILED.getValue());
            }
        }else {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "无需审核");
        }
        // 3.填充审核说明
        oldJobApplication.setAuditExplain(auditRequest.getReason());


        // 4.操作数据库
        boolean result = jobApplicationService.updateById(oldJobApplication);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    /**
     * 分页获取岗位申请列表（封装类）
     *
     * @param jobApplicationQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<JobApplicationVO>> listJobApplicationVOByPage(@RequestBody JobApplicationQueryRequest jobApplicationQueryRequest) {
        long current = jobApplicationQueryRequest.getCurrent();
        long size = jobApplicationQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        User loginUser = userService.getLoginUser();
        Page<JobApplication> jobApplicationPage = jobApplicationService.page(new Page<>(current, size),
                jobApplicationService.getQueryWrapper(jobApplicationQueryRequest, loginUser));
        // 获取封装类
        return ResultUtils.success(jobApplicationService.getJobApplicationVOPage(jobApplicationPage));
    }


    /**
     * 编辑岗位申请（给用户使用）
     *
     * @param jobApplicationEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editJobApplication(@Valid @RequestBody JobApplicationEditRequest jobApplicationEditRequest, HttpServletRequest request) {
        if (jobApplicationEditRequest == null || jobApplicationEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        JobApplication jobApplication = new JobApplication();
        BeanUtils.copyProperties(jobApplicationEditRequest, jobApplication);


        User loginUser = userService.getLoginUser();
        // 判断是否存在
        long id = jobApplicationEditRequest.getId();
        JobApplication oldJobApplication = jobApplicationService.getById(id);
        ThrowUtils.throwIf(oldJobApplication == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人可编辑
        if (!oldJobApplication.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 只能编辑待处理的
        if (!Objects.equals(oldJobApplication.getStatus(), AddStatusEnum.IN_REVIEW.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "已处理，不可编辑");
        }

        // 转为json字符串，填充文件列表,
        List<Object> fileList = jobApplicationEditRequest.getFileList();
        if(fileList != null){
            jobApplication.setFileList(JSONUtil.toJsonStr(fileList));
        }

        // 操作数据库
        boolean result = jobApplicationService.updateById(jobApplication);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * @description 获取面试通过的用户
     * @author luobin YL586246
     * @date 2025/4/29 19:49
     */
    @GetMapping("/get/interview/passed")
    public BaseResponse<List<UserVO>> getInterviewPassedUser() {
        List<UserVO> userVOList = jobApplicationService.getInterviewPassedUser();
        return ResultUtils.success(userVOList);
    }



    // endregion
}
