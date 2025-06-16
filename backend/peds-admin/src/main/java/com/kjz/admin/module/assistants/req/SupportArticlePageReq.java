package com.kjz.admin.module.assistants.req;

import lombok.Data;

@Data
public class SupportArticlePageReq {
    private int pageNum = 1;
    private int pageSize = 10;
    private String title; // 可选：按标题模糊查询
} 