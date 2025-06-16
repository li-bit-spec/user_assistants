package com.kjz.admin.module.assistants.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ManualArticleDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 