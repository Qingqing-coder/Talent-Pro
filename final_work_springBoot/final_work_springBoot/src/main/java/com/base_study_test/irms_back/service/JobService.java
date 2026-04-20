package com.base_study_test.irms_back.service;

import com.base_study_test.irms_back.entity.Job;

import java.util.List;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-14
 */
public interface JobService {
    /**
     * 分页查询所有职位信息
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<Job> selectAllJob(int pageNum, int pageSize);

    /**
     * 分页条件查询职位信息
     * @param condition
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<Job> selectByCondition(Job condition, String sortField, String sortOrder, int pageNum, int pageSize);

    /**
     * 更新职位信息
     * @param job
     * @return
     */
    boolean updateJob(Job job);

    /**
     * 删除职位信息
     * @param id
     * @return
     */
    boolean deleteById(Integer id);

    /**
     * 删除职位信息
     * @param companyName
     * @return
     */
    boolean deleteByCompanyName(String companyName);

    /**
     * 添加职位信息
     * @param job
     * @return
     */
    boolean addJob(Job job);

    /**
     * 根据最高薪资从大到小获取职位信息
     * @return List<Job>
     */
    List<Job> selectByMaxSalary();

    /**
     * 根据职位id从大到小获取职位信息
     * @return List<Job>
     */
    List<Job> selectByIdDesc();
}
