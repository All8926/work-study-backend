package com.app.project.service;


import com.app.project.model.dto.hiringRecord.HiringRecordQueryRequest;
import com.app.project.model.dto.salary.SalaryAddRequest;
import com.app.project.model.dto.salary.SalaryQueryRequest;
import com.app.project.model.entity.HiringRecord;
import com.app.project.model.entity.Salary;
import com.app.project.model.entity.User;
import com.app.project.model.vo.HiringRecordVO;
import com.app.project.model.vo.SalaryVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【salary(薪酬)】的数据库操作Service
* @createDate 2025-05-03 14:18:40
*/
public interface SalaryService extends IService<Salary> {

    /**
     * @description 新增
     * @author luobin YL586246
     * @date 2025/5/3 15:01
     */
    Boolean addSalary(SalaryAddRequest salaryAddRequest);

    /**
     * @description 获取查询条件
     * @author luobin YL586246
     * @date 2025/4/29 20:43
     */
    QueryWrapper<Salary> getQueryWrapper(SalaryQueryRequest salaryQueryRequest, User loginUser);

    /**
     * @description 获取分页数据
     * @author luobin YL586246
     * @date 2025/4/29 20:43
     */
    Page<SalaryVO> getSalaryVOPage(Page<Salary> hiringRecordPage);
}
