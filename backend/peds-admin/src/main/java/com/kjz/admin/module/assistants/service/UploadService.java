package com.kjz.admin.module.assistants.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    String uploadFile(MultipartFile file) throws Exception;
} 