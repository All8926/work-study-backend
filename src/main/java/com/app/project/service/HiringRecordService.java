package com.app.project.service;


import com.app.project.model.dto.hiringRecord.HiringRecordAddRequest;
import com.app.project.model.dto.hiringRecord.HiringRecordQueryRequest;
import com.app.project.model.entity.HiringRecord;
import com.app.project.model.entity.User;
import com.app.project.model.vo.HiringRecordVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【hiring_record】的数据库操作Service
* @createDate 2025-04-29 20:14:15
*/
public interface HiringRecordService extends IService<HiringRecord> {

    /**
     * @description 获取查询条件
     * @author luobin YL586246
     * @date 2025/4/29 20:43
     */
    QueryWrapper<HiringRecord> getQueryWrapper(HiringRecordQueryRequest hiringRecordQueryRequest, User loginUser);

    /**
     * @description 获取分页数据
     * @author luobin YL586246
     * @date 2025/4/29 20:43
     */
    Page<HiringRecordVO> getHiringRecordVOPage(Page<HiringRecord> hiringRecordPage);

    /**
     * @description 添加录用记录
     * @author luobin YL586246
     * @date 2025/4/29 21:18
     */
    Boolean addHiringRecord(HiringRecordAddRequest hiringRecordAddRequest);
}
