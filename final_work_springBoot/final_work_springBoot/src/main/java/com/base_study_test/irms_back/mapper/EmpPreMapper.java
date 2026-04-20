package com.base_study_test.irms_back.mapper;

import com.base_study_test.irms_back.entity.Emp;
import com.base_study_test.irms_back.entity.EmpPre;
import com.base_study_test.irms_back.entity.Job;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-17
 */
@Mapper
public interface EmpPreMapper {
    /**
     * 根据id查找收藏关系
     */
    EmpPre selectById(Integer preferenceId);

    /**
     * 根据用户名查找所有收藏关系
     */
    List<Job> selectByUsername(String username);

    /**
     * 根据工作id查找收藏关系
     */
    List<Emp> selectByJobId(Integer jobId);

    /**
     * 添加收藏关系
     */
    int addEmpPre(EmpPre empPre);

    /**
     * 删除收藏关系
     */
    int deleteEmpPre(String username, Integer jobId);
}
