package com.base_study_test.irms_back.service.impl;

import com.base_study_test.irms_back.entity.Emp;
import com.base_study_test.irms_back.mapper.EmpMapper;
import com.base_study_test.irms_back.service.EmpService;
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
public class EmpServiceImpl implements EmpService {
    private final EmpMapper empMapper;

    public EmpServiceImpl(EmpMapper empMapper) {
        this.empMapper = empMapper;
    }

    @Override
    public List<Emp> selectAllEmp(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return empMapper.selectAllEmp();
    }

    @Override
    public List<Emp> selectByCondition(Emp condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return empMapper.selectByCondition(condition);
    }

    @Override
    public boolean updateEmp(Emp emp) {
        empMapper.updateEmpPwd(emp.getPassword(), emp.getUsername());
        return empMapper.updateEmp(emp) > 0;
    }

    @Override
    public boolean deleteByUsername(String username) {
        return empMapper.deleteByUsername(username) > 0;
    }

    @Override
    public boolean addEmp(Emp emp) {
        return empMapper.addEmp(emp) > 0;
    }
}
