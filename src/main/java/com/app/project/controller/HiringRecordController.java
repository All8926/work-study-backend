package com.app.project.controller;

import cn.hutool.json.JSONUtil;
import com.app.project.common.*;
import com.app.project.exception.BusinessException;
import com.app.project.exception.ThrowUtils;
import com.app.project.model.dto.hiringRecord.HiringRecordAddRequest;
import com.app.project.model.dto.hiringRecord.HiringRecordEditRequest;
import com.app.project.model.dto.hiringRecord.HiringRecordQueryRequest;
import com.app.project.model.entity.HiringRecord;
import com.app.project.model.entity.User;
import com.app.project.model.vo.HiringRecordVO;
import com.app.project.service.HiringRecordService;
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
 * 录用记录接口
 *
 * @author
 * @from
 */
@RestController
@RequestMapping("/hiringRecord")
@Slf4j
public class HiringRecordController {

    @Resource
    private HiringRecordService hiringRecordService;

    @Resource
    private JobPostService jobPostService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建录用记录
     *
     * @param hiringRecordAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addHiringRecord(@Valid @RequestBody HiringRecordAddRequest hiringRecordAddRequest) {
        ThrowUtils.throwIf(hiringRecordAddRequest == null, ErrorCode.PARAMS_ERROR);

        Boolean result = hiringRecordService.addHiringRecord(hiringRecordAddRequest);
        return ResultUtils.success(result);
    }

    /**
     * 删除录用记录
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteHiringRecord(@Valid @RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        HiringRecord oldHiringRecord = hiringRecordService.getById(id);
        ThrowUtils.throwIf(oldHiringRecord == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人可删除
        if (!oldHiringRecord.getEnterPriseId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = hiringRecordService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    /**
     * 分页获取录用记录列表（封装类）
     *
     * @param hiringRecordQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<HiringRecordVO>> listHiringRecordVOByPage(@RequestBody HiringRecordQueryRequest hiringRecordQueryRequest) {
        long current = hiringRecordQueryRequest.getCurrent();
        long size = hiringRecordQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        User loginUser = userService.getLoginUser();
        Page<HiringRecord> hiringRecordPage = hiringRecordService.page(new Page<>(current, size),
                hiringRecordService.getQueryWrapper(hiringRecordQueryRequest, loginUser));
        // 获取封装类
        return ResultUtils.success(hiringRecordService.getHiringRecordVOPage(hiringRecordPage));
    }


    /**
     * 编辑录用记录（给用户使用）
     *
     * @param hiringRecordEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editHiringRecord(@Valid @RequestBody HiringRecordEditRequest hiringRecordEditRequest, HttpServletRequest request) {
        if (hiringRecordEditRequest == null || hiringRecordEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        HiringRecord hiringRecord = new HiringRecord();
        BeanUtils.copyProperties(hiringRecordEditRequest, hiringRecord);


        User loginUser = userService.getLoginUser();
        // 判断是否存在
        long id = hiringRecordEditRequest.getId();
        HiringRecord oldHiringRecord = hiringRecordService.getById(id);
        ThrowUtils.throwIf(oldHiringRecord == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人可编辑
        if (!oldHiringRecord.getEnterPriseId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }


        // 转为json字符串，填充文件列表,
        List<Object> fileList = hiringRecordEditRequest.getFileList();
        if (fileList != null) {
            hiringRecord.setFileList(JSONUtil.toJsonStr(fileList));
        }

        // 操作数据库
        boolean result = hiringRecordService.updateById(hiringRecord);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    // endregion
}
