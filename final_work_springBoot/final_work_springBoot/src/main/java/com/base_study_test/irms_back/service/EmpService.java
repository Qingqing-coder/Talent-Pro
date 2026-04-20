package com.base_study_test.irms_back.service;

import com.base_study_test.irms_back.entity.Emp;

import java.util.List;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
public interface EmpService {

    /**
     * 分页获取所有求职者
     * @param pageNum,pageSize
     * @return List<Emp>
     */
    List<Emp> selectAllEmp(int pageNum, int pageSize);

    /**
     * 条件查询求职者
     * @param condition,pageNum,pageSize
     * @return List<Emp>
     */
    List<Emp> selectByCondition(Emp condition, int pageNum, int pageSize);

    /**
     * 更新求职者信息
     * @param emp
     * @return boolean
     */
    boolean updateEmp(Emp emp);

    /**
     * 删除求职者
     * @param username
     * @return boolean
     */
    boolean deleteByUsername(String username);

    /**
     * 增加求职者信息
     * @param emp
     * @return boolean
     */
    boolean addEmp(Emp emp);
}
