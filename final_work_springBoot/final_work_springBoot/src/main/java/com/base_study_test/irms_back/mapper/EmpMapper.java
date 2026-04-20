package com.base_study_test.irms_back.mapper;

import com.base_study_test.irms_back.entity.Emp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Mapper
public interface EmpMapper {
    /**
     * 获取所有求职者
     * @return List<Emp>
     */
    List<Emp> selectAllEmp();

    /**
     * 条件查询求职者
     * @param condition
     * @return List<Emp>
     */
    List<Emp> selectByCondition(Emp condition);

    /**
     * 更新求职者信息
     * @param emp
     * @return boolean
     */
    int updateEmp(Emp emp);

    /**
     * 更新求职者密码同步
     */
    int updateEmpPwd(String password, String username);

    /**
     * 根据用户名删除求职者
     * @param username
     * @return boolean
     */
    int deleteByUsername(String username);

    /**
     * 增加求职者信息
     * @param emp
     * @return boolean
     */
    int addEmp(Emp emp);

    /**
     * 根据用户名获得头像路径
     * @param username
     */
    String selectPhotoByUsername(String username);

    /**
     * 根据用户名存储头像存储路径
     * @param username
     */
    int updatePhotoByUsername(String username, String photo);

    /**
     * 根据用户名查询证件照路径
     */
    String selectIDPhotoByUsername(@Param("username") String username);  // 证件照查询

    /**
     * 根据用户名更新证件照路径
     */
    void updateIDPhotoByUsername(@Param("username") String username, @Param("photoPath") String photoPath);  // 证件照更新
}
