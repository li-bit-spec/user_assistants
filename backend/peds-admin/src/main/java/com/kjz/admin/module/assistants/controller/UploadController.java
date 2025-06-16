package com.kjz.admin.module.assistants.controller;

import com.kjz.admin.module.assistants.service.UploadService;
import com.kjz.common.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class UploadController {
    
    @Autowired
    private UploadService uploadService;
    
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("接收到文件上传请求，文件名：{}", file.getOriginalFilename());
        try {
            String url = uploadService.uploadFile(file);
            log.info("文件上传成功，URL：{}", url);
            return Result.success(url);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
} 