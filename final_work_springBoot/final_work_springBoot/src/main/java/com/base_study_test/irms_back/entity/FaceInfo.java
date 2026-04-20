package com.base_study_test.irms_back.entity;// src/main/java/com/base_study_test/irms_back/entity/FaceInfo.java
import lombok.Data; // 添加此导入语句
@Data
public class FaceInfo {
    private String username; // 关联用户名
    private String facePath; // 人脸图像保存路径
    private int label; // 模型训练用标签
}