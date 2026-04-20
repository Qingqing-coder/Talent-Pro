package com.base_study_test.irms_back.controller;

import com.base_study_test.irms_back.common.Result;
import com.base_study_test.irms_back.entity.Emp;
import com.base_study_test.irms_back.entity.EmpPre;
import com.base_study_test.irms_back.entity.Job;
import com.base_study_test.irms_back.service.EmpPreService;
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
 * @since 2025-07-17
 */
@Tag(name = "收藏关系管理")
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/emp_pre")
public class EmpPreController {
    private final EmpPreService empPreService;


    @Operation(summary = "根据收藏关系id查询收藏关系")
    @GetMapping("/selectById")
    public Result<EmpPre> selectById(Integer preferenceId) {
        EmpPre empPre = empPreService.selectById(preferenceId);
        if (empPre != null) {
            return Result.success(empPre);
        }
        return Result.error("未找到该收藏关系");

    }
    @Operation(summary = "根据用户名分页查询所有收藏关系")
    @GetMapping("/allByUsername")
    public Result<PageInfo<Job>> selectByUsername(String username, int pageNum, int pageSize){
        List <Job> empPreList = empPreService.selectByUsername(username, pageNum, pageSize);
        if(empPreList == null || empPreList.isEmpty()){
            return Result.error("该用户未收藏任何岗位");
        }
        return Result.success(PageInfo.of(empPreList));
    }

    @Operation(summary = "根据工作id分页查询所有收藏关系")
    @GetMapping("/selectByJobId")
    public Result<PageInfo<Emp>> selectByJobId( Integer jobId, int pageNum, int pageSize){
        List <Emp> empPreList = empPreService.selectByJobId(jobId, pageNum, pageSize);
        if(empPreList == null || empPreList.isEmpty()){
            return Result.error("该工作未被任何用户收藏");
        }
        return Result.success(PageInfo.of(empPreList));
    }

    @Operation(summary = "添加收藏关系")
    @PostMapping("/addEmpPre")
    public Result<String> addEmpPre(@RequestBody EmpPre empPre){
        return empPreService.addEmpPre(empPre) ? Result.success("添加成功") : Result.error("添加失败");
    }

    @Operation(summary = "删除收藏关系")
    @DeleteMapping("/deleteEmpPre")
    public Result<String> deleteEmpPre(@RequestParam String username, @RequestParam Integer jobId){
        return empPreService.deleteEmpPre(username, jobId) ? Result.success("删除成功") : Result.error("删除失败");
    }

    @Operation(summary = "判断用户是否收藏该工作")
    @GetMapping("/isExistEmpPre")
    public Result<Boolean> isExistEmpPre(@RequestParam String username, @RequestParam Integer jobId){
        return Result.success(empPreService.isExistEmpPre(username, jobId));
    }
}
