package com.base_study_test.irms_back.controller;

import com.base_study_test.irms_back.common.Result;
import com.base_study_test.irms_back.entity.Company;
import com.base_study_test.irms_back.entity.Job;
import com.base_study_test.irms_back.service.CompanyService;
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
@Tag(name = "公司信息管理")
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyController {
    private final CompanyService companyService;

    @Operation(summary = "分页查询所有公司信息")
    @GetMapping("/all")
    public Result<PageInfo<Company>> selectAllcompany(int pageNum, int pageSize) {
        List<Company> companyList = companyService.selectAllCompany(pageNum, pageSize);
        if(companyList == null || companyList.isEmpty()){
            return Result.error("查询操作失败，没有数据");
        }
        return Result.success(PageInfo.of(companyList));
    }

    @Operation(summary = "分页条件查询公司信息")
    @GetMapping("/selectWithCondition")
    public Result<PageInfo<Company>> selectWithCondition(@RequestParam(required = false) Integer id,
                                                         @RequestParam(required = false) String username,
                                                          @RequestParam(required = false) String nickname,
                                                          @RequestParam(required = false) String password,
                                                         int pageNum,
                                                         int pageSize) {
        Company condition = new Company();
        condition.setId(id);
        condition.setUsername(username);
        condition.setNickname(nickname);
        condition.setPassword(password);
        List<Company> companyList = companyService.selectByCondition(condition, pageNum, pageSize);
        if(companyList == null || companyList.isEmpty()){
            return Result.error("查询操作失败，没有数据");
        }
        return Result.success(PageInfo.of(companyList));
    }

    @Operation(summary = "更新公司信息")
    @PutMapping("/update")
    public Result<String> updateCompany(@RequestBody Company company) {
        boolean flag = companyService.updateCompany(company);
        if(flag){
            return Result.success("更新操作成功");
        }
        return Result.error("更新操作失败");
    }

    @Operation(summary = "删除公司信息")
    @DeleteMapping("/delete")
    public Result<String> deleteCompany(@RequestParam String username) {
        boolean flag = companyService.deleteByUsername(username);
        if(flag){
            return Result.success("删除操作成功");
        }
        return Result.error("删除操作失败");
    }

    @Operation(summary = "添加公司信息")
    @PostMapping("/add")
    public Result<String> addCompany(@RequestBody Company company) {
        boolean flag = companyService.addCompany(company);
        if(flag){
            return Result.success("添加操作成功");
        }
        return Result.error("添加操作失败");
    }

    @Operation(summary = "根据公司username查询公司所有岗位")
    @GetMapping("/selectAllJobByUsername")
    public Result<List<Job>> selectAllJobByUsername(String username) {
        List<Job> jobs = companyService.selectAllJobByUsername(username);
        if (jobs == null || jobs.isEmpty()){
            return Result.error("查询操作失败，没有数据");
        }
        return Result.success(jobs);
    }
}
