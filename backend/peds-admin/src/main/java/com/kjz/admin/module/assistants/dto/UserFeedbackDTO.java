package com.kjz.admin.module.assistants.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;

@Data
public class UserFeedbackDTO {
    private Long id;
    private String content;
    
    @JsonProperty("created_at")
    private Date createdAt;
    
    @JsonProperty("updated_at")
    private Date updatedAt;
    
    @JsonProperty("imageUrls")
    private List<String> imageUrls; // 图片URL列表
} 