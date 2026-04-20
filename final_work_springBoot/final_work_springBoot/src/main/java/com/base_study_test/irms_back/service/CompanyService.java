package com.base_study_test.irms_back.service;

import com.base_study_test.irms_back.entity.Company;
import com.base_study_test.irms_back.entity.Job;

import java.util.List;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
public interface CompanyService {
    /**
     * 分页获取所有公司
     * @param pageNum,pageSize
     * @return List<Company>
     */
    List<Company> selectAllCompany(int pageNum, int pageSize);

    /**
     * 条件查询公司
     * @param condition,pageNum,pageSize
     * @return List<Company>
     */
    List<Company> selectByCondition(Company condition, int pageNum, int pageSize);

    /**
     * 更新公司信息
     * @param company
     * @return boolean
     */
    boolean updateCompany(Company company);

    /**
     * 删除公司
     * @param username
     * @return boolean
     */
    boolean deleteByUsername(String username);

    /**
     * 增加公司信息
     * @param company
     * @return boolean
     */
    boolean addCompany(Company company);

    /**
     * 获取公司所有岗位
     */
    List<Job> selectAllJobByUsername(String username);
}
