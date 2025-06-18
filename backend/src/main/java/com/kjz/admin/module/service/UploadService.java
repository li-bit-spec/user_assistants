package com.kjz.admin.module.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    String uploadFile(MultipartFile file) throws Exception;
} 