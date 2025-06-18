package com.module.service.impl;

import com.module.service.AssUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class AssUploadServiceImpl implements AssUploadService {

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public String uploadFile(MultipartFile file) {
        log.info("开始上传文件，文件名：{}，大小：{}", file.getOriginalFilename(), file.getSize());
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            log.error("不支持的文件类型：{}", contentType);
            throw new RuntimeException("只支持上传图片文件");
        }
        
        // 检查文件大小
        if (file.getSize() > 2 * 1024 * 1024) {
            log.error("文件大小超过限制：{}", file.getSize());
            throw new RuntimeException("文件大小不能超过2MB");
        }
        
        try {
            // 创建上传目录
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    log.error("创建上传目录失败：{}", uploadPath);
                    throw new RuntimeException("创建上传目录失败");
                }
                log.info("创建上传目录成功：{}", uploadPath);
            }
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            log.info("生成文件名：{}", filename);
            
            // 保存文件
            File destFile = new File(dir, filename);
            try {
                file.transferTo(destFile);
                log.info("文件保存成功：{}", destFile.getAbsolutePath());
            } catch (IOException e) {
                log.error("文件保存失败", e);
                throw new RuntimeException("文件保存失败: " + e.getMessage());
            }
            
            // 返回文件URL
            String fileUrl = "/api/uploads/" + filename;
            log.info("返回文件URL：{}", fileUrl);
            return fileUrl;
        } catch (Exception e) {
            log.error("文件上传过程发生异常", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
} 