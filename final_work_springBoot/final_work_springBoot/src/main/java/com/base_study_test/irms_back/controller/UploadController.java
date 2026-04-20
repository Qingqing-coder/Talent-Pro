package com.base_study_test.irms_back.controller;

import com.base_study_test.irms_back.common.Result;
import com.base_study_test.irms_back.mapper.EmpMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * @author kczx
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-20
 */
@Slf4j
@Tag(name = "上传文件管理")
@CrossOrigin
@RestController
public class UploadController {
    private final EmpMapper empMapper;
    private static final String IMAGE_DIR = "D:/other/user_photo/";

    public UploadController(EmpMapper empMapper) {
        this.empMapper = empMapper;
    }

    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    public Result<String> upload(@RequestParam MultipartFile file, @RequestParam String username) {
        log.info("上传成功,文件：{}", file);
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        log.info("文件名：{}", fileName);

        try {
            // 确保目标目录存在
            File dest = new File("D:/other/user_photo/" + fileName);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs(); // 创建父目录
            }
            empMapper.updatePhotoByUsername(username, dest.getName());
            file.transferTo(dest);
            return Result.success("上传成功");
        } catch (Exception e) {
            log.error("文件上传失败：", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 生成唯一文件名
     * @param originalFilename 原始文件名
     * @return 唯一文件名（含UUID）
     */
    private String generateUniqueFileName(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return UUID.randomUUID().toString();
        }

        String baseName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));

        return baseName + "_" + UUID.randomUUID() + extension;
    }

    @GetMapping("/getFilePath")
    @Operation(summary = "获取文件路径")
    public Result<String> getFilePath(@RequestParam String username) {
        return Result.success(empMapper.selectPhotoByUsername(username));
    }

    @GetMapping("/getImage")
    @Operation(summary = "获取图片")
    public ResponseEntity<Resource> getImage(@RequestParam String filename) throws IOException {
        Path filePath = Path.of(IMAGE_DIR + filename);
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(filePath);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(filePath))
                .header("Access-Control-Allow-Origin", "*") // 明确允许跨域
                .body(resource);
    }
}
