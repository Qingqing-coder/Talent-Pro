package com.base_study_test.irms_back.service;

import com.base_study_test.irms_back.entity.Emp;
import com.base_study_test.irms_back.entity.EmpPre;
import com.base_study_test.irms_back.entity.Job;

import java.util.List;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-17
 */
public interface EmpPreService {

    /**
     * 根据id查找收藏关系
     */
    EmpPre selectById(Integer preferenceId);

    /**
     * 根据用户名查找所有收藏关系
     */
    List<Job> selectByUsername(String username, int pageNum, int pageSize);

    /**
     * 根据工作id查找收藏关系
     */
    List<Emp> selectByJobId(Integer jobId, int pageNum, int pageSize);

    /**
     * 添加收藏关系
     */
    boolean addEmpPre(EmpPre empPre);

    /**
     * 删除收藏关系
     */
    boolean deleteEmpPre(String username, Integer jobId);

    /**
     * 检测收藏关系是否存在
     */
    boolean isExistEmpPre(String username, Integer jobId);

}
