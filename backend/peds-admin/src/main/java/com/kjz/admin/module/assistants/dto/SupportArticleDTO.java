package com.kjz.admin.module.assistants.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SupportArticleDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 