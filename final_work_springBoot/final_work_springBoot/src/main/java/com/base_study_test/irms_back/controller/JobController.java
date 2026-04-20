package com.base_study_test.irms_back.controller;

import com.base_study_test.irms_back.common.Result;
import com.base_study_test.irms_back.entity.Job;
import com.base_study_test.irms_back.service.JobService;
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
 * @since 2025-07-14
 */
@Tag(name = "职位信息管理")
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/job")
public class JobController {
    private final JobService jobService;

    @Operation(summary = "分页查询所有职位信息")
    @GetMapping("/all")
    public Result<PageInfo<Job>> selectAllJob(int pageNum, int pageSize) {
        List<Job> jobs = jobService.selectAllJob(pageNum, pageSize);
        if (jobs == null || jobs.isEmpty()){
            return Result.error("查询操作失败，没有数据");
        }
        return Result.success(PageInfo.of(jobs));
    }

    @Operation(summary = "分页条件查询职位信息")
    @GetMapping("/selectWithCondition")
    public Result<PageInfo<Job>> selectWithCondition(
                                                    @RequestParam(required = false) Integer id,
                                                    @RequestParam(required = false) String title,
                                                     @RequestParam(required = false) String salary,
                                                     @RequestParam(required = false) String companyName,
                                                     @RequestParam(required = false) String companySize,
                                                     @RequestParam(required = false) String fullLocation,
                                                      @RequestParam(required = false) String experience,
                                                     @RequestParam(required = false) String education,
                                                     @RequestParam(required = false) String type,
                                                     @RequestParam(required = false) String benefits,
                                                    @RequestParam(required = false) String jobType,
                                                     @RequestParam(required = false) String sortField,
                                                     @RequestParam(required = false) String sortOrder,
                                                     int pageNum,
                                                     int pageSize) {
        Job condition = new Job();
        condition.setId(id);
        condition.setTitle(title);
        condition.setSalary(salary);
        condition.setCompanyName(companyName);
        condition.setCompanySize(companySize);
        condition.setFullLocation(fullLocation);
        condition.setExperience(experience);
        condition.setEducation(education);
        condition.setType(type);
        condition.setBenefits(benefits);
        condition.setJobType(jobType);
        List<Job> jobs = jobService.selectByCondition(condition, sortField, sortOrder, pageNum, pageSize);
        if (jobs == null || jobs.isEmpty()){
            return Result.error("查询操作失败，没有数据");
        }
        return Result.success(PageInfo.of(jobs));
    }

    @Operation(summary = "更新职位信息")
    @PutMapping("/update")
    public Result<String> updateJob(@RequestBody Job job) {
        boolean update = jobService.updateJob(job);
        if (!update){
            return Result.error("更新操作失败");
        }
        return Result.success("更新操作成功");
    }

    @Operation(summary = "删除根据岗位id职位信息")
    @DeleteMapping("/delete")
    public Result<String> deleteJob(@RequestParam Integer id) {
        boolean delete = jobService.deleteById(id);
        if (!delete){
            return Result.error("删除操作失败");
        }
        return Result.success("删除操作成功");
    }

    @Operation(summary = "删除根据公司名字职位信息")
    @DeleteMapping("/deleteByCompanyName")
    public Result<String> deleteJob(@RequestParam String companyName) {
        boolean delete = jobService.deleteByCompanyName(companyName);
        if (!delete){
            return Result.error("删除操作失败");
        }
        return Result.success("删除操作成功");
    }

    @Operation(summary = "添加职位信息")
    @PostMapping("/add")
    public Result<String> addJob(@RequestBody Job job) {
        boolean add = jobService.addJob(job);
        if (!add){
            return Result.error("添加操作失败");
        }
        return Result.success("添加操作成功");
    }

    @Operation(summary = "根据最高薪从大到小获取职位信息")
    @GetMapping("/selectByMaxSalary")
    public Result<List<Job>> selectByMaxSalary() {
        List<Job> jobs = jobService.selectByMaxSalary();
        if (jobs == null || jobs.isEmpty()){
            return Result.error("查询操作失败，没有数据");
        }
        return Result.success(jobs);
    }

    @Operation(summary = "根据id从大到小获取职位信息")
    @GetMapping("/selectByIdDesc")
    public Result<List<Job>> selectByIdDesc() {
        List<Job> jobs = jobService.selectByIdDesc();
        if (jobs == null || jobs.isEmpty()){
            return Result.error("查询操作失败，没有数据");
        }
        return Result.success(jobs);
    }
}
