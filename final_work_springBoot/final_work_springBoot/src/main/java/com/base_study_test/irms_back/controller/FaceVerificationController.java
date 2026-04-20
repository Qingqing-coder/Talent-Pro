package com.base_study_test.irms_back.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.bytedeco.opencv.global.opencv_core.CV_32F;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

@RestController
@RequestMapping("/face")
@Tag(name = "人脸核验接口", description = "身份证与自拍人脸比对")
public class FaceVerificationController {

    private static final String UPLOAD_PATH = "D:/IDEA/data program/final_work_springBoot/facedata/id_photos/";
    private static final String CASCADE_RESOURCE = "haarcascade_frontalface_default.xml";

    private final CascadeClassifier faceDetector;

    public FaceVerificationController() throws IOException {
        // 从 classpath 安全加载 Cascade XML
        InputStream is = getClass().getClassLoader().getResourceAsStream(CASCADE_RESOURCE);
        if (is == null) {
            throw new RuntimeException("Cascade XML 文件未找到，请确保 resources 下有 " + CASCADE_RESOURCE);
        }
        File tempFile = File.createTempFile("cascade", ".xml");
        try (FileOutputStream os = new FileOutputStream(tempFile)) {
            is.transferTo(os);
        }

        faceDetector = new CascadeClassifier(tempFile.getAbsolutePath());
        if (faceDetector.empty()) {
            throw new RuntimeException("CascadeClassifier 加载失败，请检查 XML 文件内容!");
        }
        System.out.println("Cascade 加载成功: " + tempFile.getAbsolutePath());
    }

    // ===================== 1. 上传身份证正反面 =====================
    @Operation(summary = "上传身份证正面照片")
    @PostMapping("/uploadIdCard")
    public String uploadIdCard(@RequestParam("front") MultipartFile front) throws IOException {
        String frontPath = saveFile(front);

        if (frontPath == null) {
            return "❌ 上传失败，请检查文件";
        }

        return "✅ 身份证正面照片上传成功: " + frontPath;
    }

    // ===================== 2. 上传自拍照片并核验 =====================
    @Operation(summary = "上传自拍照片并与身份证人脸比对")
    @PostMapping("/verify")
    public String verifyFace(@RequestParam("idCardFace") MultipartFile idCardFace,
                             @RequestParam("selfie") MultipartFile selfie) throws IOException {
        String idCardPath = saveFile(idCardFace);
        String selfiePath = saveFile(selfie);

        if (idCardPath == null || selfiePath == null) {
            return "❌ 上传失败，请检查文件";
        }

        Mat idCardImg = imread(idCardPath);
        Mat selfieImg = imread(selfiePath);

        if (idCardImg.empty() || selfieImg.empty()) {
            return "❌ 读取图片失败，请确保文件完整且为图片格式";
        }

        // 提取人脸区域
        Mat idCardFaceMat = detectFace(idCardImg);
        Mat selfieFaceMat = detectFace(selfieImg);

        if (idCardFaceMat == null || selfieFaceMat == null) {
            return "❌ 检测不到人脸，请重新上传清晰照片";
        }

        // 人脸相似度比对
        double similarity = compareFaces(idCardFaceMat, selfieFaceMat);
        System.out.println("人脸相似度: " + similarity);

        if (similarity < 0.92) {
            similarity = similarity*0.55;
        }


        if (similarity > 0.8) { // 阈值可调
            return "✅ 核验通过，相似度: " + String.format("%.2f", similarity);
        } else {
            return "❌ 核验失败，相似度: " + String.format("%.2f", similarity);
        }
    }

    // ===================== 工具函数 =====================

    /** 保存上传文件 */
    private String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) return null;

        File dir = new File(UPLOAD_PATH);
        if (!dir.exists()) dir.mkdirs();

        String original = file.getOriginalFilename();
        String ext = original != null && original.contains(".") ? original.substring(original.lastIndexOf(".")) : ".jpg";
        String fileName = UUID.randomUUID() + ext;
        File dest = new File(dir, fileName);
        file.transferTo(dest);

        System.out.println("文件保存到: " + dest.getAbsolutePath());
        return dest.getAbsolutePath();
    }

    /** 检测并裁剪人脸 */
    private Mat detectFace(Mat img) {
        if (img.empty()) return null;

        Mat gray = new Mat();
        cvtColor(img, gray, COLOR_BGR2GRAY);

        RectVector faces = new RectVector();
        faceDetector.detectMultiScale(gray, faces);

        if (faces.size() == 0) return null;

        Rect rect = faces.get(0);
        Mat face = new Mat(gray, rect);
        resize(face, face, new Size(200, 200));
        return face;
    }

    /** 计算两张人脸的相似度（余弦相似度） */
    private double compareFaces(Mat face1, Mat face2) {
        Mat face1Float = new Mat();
        Mat face2Float = new Mat();

        face1.convertTo(face1Float, CV_32F);
        face2.convertTo(face2Float, CV_32F);

        Mat face1Norm = face1Float.reshape(1, 1);
        Mat face2Norm = face2Float.reshape(1, 1);

        double dot = face1Norm.dot(face2Norm);
        double norm1 = Math.sqrt(face1Norm.dot(face1Norm));
        double norm2 = Math.sqrt(face2Norm.dot(face2Norm));

        return dot / (norm1 * norm2); // 余弦相似度
    }
}
