package com.base_study_test.irms_back.mapper;

import com.base_study_test.irms_back.entity.Job;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-14
 */
@Mapper
public interface JobMapper {
    /**
     * 查询所有职位信息
     * @return
     */
    List<Job> selectAllJob();

    /**
     * 条件查询职位信息
     * @param condition, sortFiled, sortOrder
     * @return
     */
    List<Job> selectByCondition(@Param("condition") Job condition,
                                @Param("sortField") String sortField,
                                @Param("sortOrder") String sortOrder);

    /**
     * 更新职位信息
     * @param job
     * @return
     */
    int updateJob(Job job);

    /**
     * 根据id删除职位信息
     * @param id
     * @return
     */
    int deleteById(Integer id);

    /**
     * 根据公司名称删除全部职位信息
     */
    int deleteByCompanyName(String companyName);

    /**
     * 添加职位信息
     * @param job
     * @return
     */
    int addJob(Job job);

    /**
     * 根据最高薪资排序查询职位信息
     * @return
     */
    List<Job> selectByMaxSalary();

    /**
     * 根据职位id从大到小查询职位信息
     * @return
     */
    List<Job> selectByIdDesc();
}
