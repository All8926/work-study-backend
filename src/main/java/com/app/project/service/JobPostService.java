package com.app.project.service;


import com.app.project.model.dto.jobPost.JobPostQueryRequest;
import com.app.project.model.entity.JobPost;
import com.app.project.model.entity.User;
import com.app.project.model.vo.JobPostVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【job_post(岗位信息)】的数据库操作Service
* @createDate 2025-04-25 13:24:49
*/
public interface JobPostService extends IService<JobPost> {

    /**
     * 获取查询条件
     * @param jobPostQueryRequest
     * @param loginUser
     * @return
     */
    QueryWrapper<JobPost> getQueryWrapper(JobPostQueryRequest jobPostQueryRequest, User loginUser);

    /**
     * 获取岗位分页数据
     * @param jobPostPage
     * @return
     */
    Page<JobPostVO> getJobPostVOPage(Page<JobPost> jobPostPage);

    /**
     * 获取岗位VO
     * @param jobPost
     * @return
     */
    JobPostVO getJobPostVO(JobPost jobPost);
}
