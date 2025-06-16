package com.kjz.admin.module.assistants.req;

import lombok.Data;

@Data
public class UserFeedbackPageReq {
    private int pageNum = 1;
    private int pageSize = 10;
} 