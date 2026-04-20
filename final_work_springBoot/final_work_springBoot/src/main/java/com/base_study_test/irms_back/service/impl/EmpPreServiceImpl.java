package com.base_study_test.irms_back.service.impl;

import com.base_study_test.irms_back.entity.Emp;
import com.base_study_test.irms_back.entity.EmpPre;
import com.base_study_test.irms_back.entity.Job;
import com.base_study_test.irms_back.mapper.EmpPreMapper;
import com.base_study_test.irms_back.service.EmpPreService;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-17
 */
@Service
public class EmpPreServiceImpl implements EmpPreService {
    private final EmpPreMapper empPreMapper;

    public EmpPreServiceImpl(EmpPreMapper empPreMapper) {
        this.empPreMapper = empPreMapper;
    }

    @Override
    public EmpPre selectById(Integer preferenceId) {
        return empPreMapper.selectById(preferenceId);
    }

    @Override
    public List<Job> selectByUsername(String username, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return empPreMapper.selectByUsername(username);
    }

    @Override
    public List<Emp> selectByJobId(Integer jobId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return empPreMapper.selectByJobId(jobId);
    }

    @Override
    public boolean addEmpPre(EmpPre empPre) {
        return empPreMapper.addEmpPre(empPre) > 0;
    }

    @Override
    public boolean deleteEmpPre(String username, Integer jobId) {
        return empPreMapper.deleteEmpPre(username, jobId) > 0;
    }

    @Override
    public boolean isExistEmpPre(String username, Integer jobId) {
        List<Job> jobs= empPreMapper.selectByUsername(username);
        if (jobs != null){
            for (Job job : jobs) {
                if (job.getId().equals(jobId)){
                    return true;
                }
            }
        }
        return false;
    }
}
