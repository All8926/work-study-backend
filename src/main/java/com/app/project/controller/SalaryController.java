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
import com.app.project.model.dto.salary.SalaryAddRequest;
import com.app.project.model.dto.salary.SalaryQueryRequest;
import com.app.project.model.dto.salary.SalaryUpdateRequest;
import com.app.project.model.entity.Salary;
import com.app.project.model.entity.User;
import com.app.project.model.vo.SalaryVO;
import com.app.project.service.SalaryService;
import com.app.project.service.JobPostService;
import com.app.project.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 薪酬接口
 *
 * @author
 * @from
 */
@RestController
@RequestMapping("/salary")
@Slf4j
public class SalaryController {

    @Resource
    private SalaryService salaryService;


    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建薪酬
     *
     * @param salaryAddRequest
     * @return
     */
    @PostMapping("/add")
    @SaCheckRole(UserConstant.ENTERPRISE_ROLE)
    public BaseResponse<Boolean> addSalary(@Valid @RequestBody SalaryAddRequest salaryAddRequest) {
        ThrowUtils.throwIf(salaryAddRequest == null, ErrorCode.PARAMS_ERROR);

        Boolean result = salaryService.addSalary(salaryAddRequest);
        return ResultUtils.success(result);
    }

    /**
     * 删除薪酬
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    @SaCheckRole(UserConstant.ENTERPRISE_ROLE)
    public BaseResponse<Boolean> deleteSalary(@Valid @RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        Salary oldSalary = salaryService.getById(id);
        ThrowUtils.throwIf(oldSalary == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人可删除
        if (!oldSalary.getEnterPriseId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = salaryService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    /**
     * 分页获取薪酬列表（封装类）
     *
     * @param salaryQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<SalaryVO>> listSalaryVOByPage(@RequestBody SalaryQueryRequest salaryQueryRequest) {
        long current = salaryQueryRequest.getCurrent();
        long size = salaryQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        User loginUser = userService.getLoginUser();
        Page<Salary> salaryPage = salaryService.page(new Page<>(current, size),
                salaryService.getQueryWrapper(salaryQueryRequest, loginUser));
        // 获取封装类
        return ResultUtils.success(salaryService.getSalaryVOPage(salaryPage));
    }


    /**
     * 编辑薪酬
     *
     * @param salaryUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @SaCheckRole(UserConstant.ENTERPRISE_ROLE)
    public BaseResponse<Boolean> editSalary(@Valid @RequestBody SalaryUpdateRequest salaryUpdateRequest ) {
        if (salaryUpdateRequest == null || salaryUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Salary salary = new Salary();
        BeanUtils.copyProperties(salaryUpdateRequest, salary);

        User loginUser = userService.getLoginUser();
        // 判断是否存在
        long id = salaryUpdateRequest.getId();
        Salary oldSalary = salaryService.getById(id);
        ThrowUtils.throwIf(oldSalary == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人可编辑
        if (!oldSalary.getEnterPriseId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 操作数据库
        boolean result = salaryService.updateById(salary);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }




    // endregion
}
