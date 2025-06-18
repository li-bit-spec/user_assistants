package com.module.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author 李华宪
 */
public interface UploadService {
    String uploadFile(MultipartFile file) throws Exception;
} 