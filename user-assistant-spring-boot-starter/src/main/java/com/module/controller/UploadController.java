package com.module.controller;

import com.module.service.AssUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 李华宪
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class UploadController {
    
    @Autowired
    private AssUploadService assUploadService;
    
    @PostMapping("/upload")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("接收到文件上传请求，文件名：{}", file.getOriginalFilename());
        Map<String, Object> response = new HashMap<>();
        try {
            String url = assUploadService.uploadFile(file);
            log.info("文件上传成功，URL：{}", url);
            response.put("code", 0);
            response.put("message", "success");
            response.put("data", url);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            response.put("code", 1);
            response.put("message", "文件上传失败：" + e.getMessage());
            response.put("data", null);
        }
        return response;
    }
}