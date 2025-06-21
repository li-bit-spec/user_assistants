package com.module.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

/**
 * @author 李华宪
 */
@Data
@TableName("ass_feedbacks")
public class AssUserFeedback {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String content;
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Date createdAt;
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
} 