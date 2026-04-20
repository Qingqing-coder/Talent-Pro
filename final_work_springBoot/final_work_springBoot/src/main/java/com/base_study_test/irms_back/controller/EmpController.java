package com.base_study_test.irms_back.controller;

import com.base_study_test.irms_back.common.Result;
import com.base_study_test.irms_back.entity.Emp;
import com.base_study_test.irms_back.service.EmpService;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Tag(name = "求职者信息管理")
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/emp")
public class EmpController {
    private final EmpService empService;

    @Operation(summary = "分页查询所有求职者信息")
    @GetMapping("/all")
    public Result<PageInfo<Emp>> selectAllEmp(int pageNum, int pageSize) {
        List<Emp> empList = empService.selectAllEmp(pageNum, pageSize);
        if(empList == null || empList.isEmpty()){
            return Result.error("查询操作失败，没有数据");
        }
        return Result.success(PageInfo.of(empList));
    }

    @Operation(summary = "分页条件查询求职者信息")
    @GetMapping("/selectWithCondition")
    public Result<PageInfo<Emp>> selectWithCondition(@RequestParam(required = false, defaultValue = "") String username,
                                                    @RequestParam(required = false, defaultValue = "") String nickname,
                                                    @RequestParam(required = false, defaultValue = "") String name,
                                                     @RequestParam(required = false) Integer age,
                                                      @RequestParam(required = false) Integer hasExperience,
                                                     @RequestParam(required = false) Integer sex,
                                                     @RequestParam(required = false) String major,
                                                     int pageNum,
                                                    int pageSize) {
        Emp condition = new Emp();
        condition.setUsername(username);
        condition.setNickname(nickname);
        condition.setName(name);
        condition.setAge(age);
        condition.setHasExperience(hasExperience);
        condition.setSex(sex);
        condition.setMajor(major);
        List<Emp> empList = empService.selectByCondition(condition, pageNum, pageSize);
        if(empList == null || empList.isEmpty()){
            return Result.error("查询操作失败，没有数据");
        }
        return Result.success(PageInfo.of(empList));
    }

    @Operation(summary = "更新求职者信息")
    @PutMapping("/update")
    public Result<String> updateEmp(@RequestBody Emp emp) {
        boolean flag = empService.updateEmp(emp);
        if(flag){
            return Result.success("更新操作成功");
        }
        return Result.error("更新操作失败");
    }

    @Operation(summary = "删除求职者信息")
    @DeleteMapping("/delete")
    public Result<String> deleteEmp(@RequestParam String username) {
        boolean flag = empService.deleteByUsername(username);
        if(flag){
            return Result.success("删除操作成功");
        }
        return Result.error("删除操作失败");
    }

    @Operation(summary = "添加求职者信息")
    @PostMapping("/add")
    public Result<String> addEmp(@RequestBody Emp emp) {
        boolean flag = empService.addEmp(emp);
        if(flag){
            return Result.success("添加操作成功");
        }
        return Result.error("添加操作失败");
    }
}
