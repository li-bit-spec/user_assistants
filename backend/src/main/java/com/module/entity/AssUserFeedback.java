package com.module.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("ass_user_feedbacks")
public class AssUserFeedback {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String content;
    @TableField("created_at")
    private Date createdAt;
    @TableField("updated_at")
    private Date updatedAt;
} 