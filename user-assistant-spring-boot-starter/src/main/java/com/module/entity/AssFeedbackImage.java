package com.module.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("ass_feedback_images")
public class AssFeedbackImage {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("feedback_id")
    private Long feedbackId;
    
    @TableField("image_url")
    private String imageUrl;
    
    @TableField("created_at")
    private Date createdAt;
} 