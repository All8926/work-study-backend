package com.app.project.service;


import com.app.project.model.dto.jobApplication.JobApplicationQueryRequest;
import com.app.project.model.entity.JobApplication;
import com.app.project.model.entity.User;
import com.app.project.model.vo.JobApplicationVO;
import com.app.project.model.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【job_application(岗位申请)】的数据库操作Service
* @createDate 2025-04-27 19:40:13
*/
public interface JobApplicationService extends IService<JobApplication> {

    /**
     * 获取查询条件
     * @param jobApplicationQueryRequest
     * @param loginUser
     * @return
     */
    QueryWrapper<JobApplication> getQueryWrapper(JobApplicationQueryRequest jobApplicationQueryRequest, User loginUser);

    /**
     * 获取分页数据
     * @param jobApplicationPage
     * @return
     */
    Page<JobApplicationVO> getJobApplicationVOPage(Page<JobApplication> jobApplicationPage);

    /**
     * @description 获取面试通过用户
     * @author luobin YL586246
     * @date 2025/4/29 19:50
     * @return null
     */
    List<UserVO> getInterviewPassedUser();
}
