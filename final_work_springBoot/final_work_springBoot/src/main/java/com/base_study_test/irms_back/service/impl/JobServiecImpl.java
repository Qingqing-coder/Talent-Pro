package com.base_study_test.irms_back.service.impl;

import com.base_study_test.irms_back.entity.Job;
import com.base_study_test.irms_back.mapper.JobMapper;
import com.base_study_test.irms_back.service.JobService;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-14
 */
@Service
public class JobServiecImpl implements JobService {
    private final JobMapper jobMapper;

    public JobServiecImpl(JobMapper jobMapper) {
        this.jobMapper = jobMapper;
    }
    @Override
    public List<Job> selectAllJob(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return jobMapper.selectAllJob();
    }

    @Override
    public List<Job> selectByCondition(Job condition,String sortField, String sortOrder, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return jobMapper.selectByCondition(condition, sortField, sortOrder);
    }

    @Override
    public boolean updateJob(Job job) {
        return jobMapper.updateJob(job) > 0;
    }

    @Override
    public boolean deleteById(Integer id) {
        return jobMapper.deleteById(id) > 0;
    }

    @Override
    public boolean deleteByCompanyName(String companyName) {
        return jobMapper.deleteByCompanyName(companyName) > 0;
    }

    @Override
    public boolean addJob(Job job) {
        return jobMapper.addJob(job) > 0;
    }

    @Override
    public List<Job> selectByMaxSalary() {
        return jobMapper.selectByMaxSalary();
    }

    @Override
    public List<Job> selectByIdDesc() {
        return jobMapper.selectByIdDesc();
    }
}
