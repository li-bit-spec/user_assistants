package com.kjz.common.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("user_feedbacks")
public class UserFeedback {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String content;
    @TableField("created_at")
    private Date createdAt;
    @TableField("updated_at")
    private Date updatedAt;
} 