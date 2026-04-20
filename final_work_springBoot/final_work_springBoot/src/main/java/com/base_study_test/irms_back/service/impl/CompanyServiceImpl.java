package com.base_study_test.irms_back.service.impl;

import com.base_study_test.irms_back.entity.Company;
import com.base_study_test.irms_back.entity.Job;
import com.base_study_test.irms_back.mapper.CompanyMapper;
import com.base_study_test.irms_back.service.CompanyService;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyMapper companyMapper;

    public CompanyServiceImpl(CompanyMapper companyMapper) {
        this.companyMapper = companyMapper;
    }

    @Override
    public List<Company> selectAllCompany(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return companyMapper.selectAllCompany();
    }

    @Override
    public List<Company> selectByCondition(Company condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return companyMapper.selectByCondition(condition);
    }

    @Override
    public boolean updateCompany(Company company) {
        companyMapper.updateCompanyPwd(company.getPassword(), company.getUsername());
        return companyMapper.updateCompany(company) > 0;
    }

    @Override
    public boolean deleteByUsername(String username) {
        return companyMapper.deleteByUsername(username) > 0;
    }

    @Override
    public boolean addCompany(Company company) {
        return companyMapper.addCompany(company) > 0;
    }

    @Override
    public List<Job> selectAllJobByUsername(String username) {
        return companyMapper.selectAllJobByUsername(username);
    }
}
