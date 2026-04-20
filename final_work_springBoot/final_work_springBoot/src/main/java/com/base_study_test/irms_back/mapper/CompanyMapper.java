package com.base_study_test.irms_back.mapper;

import com.base_study_test.irms_back.entity.Company;
import com.base_study_test.irms_back.entity.Job;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Mapper
public interface CompanyMapper {
    /**
     * 获取所有公司
     * @return List<Company>
     */
    List<Company> selectAllCompany();

    /**
     * 条件查询公司
     * @param condition
     * @return List<Company>
     */
    List<Company> selectByCondition(Company condition);

    /**
     * 更新公司信息
     * @param company
     * @return boolean
     */
    int updateCompany(Company company);

    /**
     * 根据用户名删除公司
     * @param username
     * @return boolean
     */
    int deleteByUsername(String username);

    /**
     * 增加公司信息
     * @param company
     * @return boolean
     */
    int addCompany(Company  company);

    /**
     * @param username
     * @discription: 根据用户名查询公司所有岗位
     */
    List<Job> selectAllJobByUsername(String username);

    /**
     * 更新公司密码同步
     */
    int updateCompanyPwd(String password, String username);
}
