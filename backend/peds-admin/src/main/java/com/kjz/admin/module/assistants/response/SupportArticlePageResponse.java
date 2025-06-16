package com.kjz.admin.module.assistants.response;

import com.kjz.admin.module.assistants.dto.SupportArticleDTO;
import lombok.Data;
import java.util.List;

@Data
public class SupportArticlePageResponse {
    private List<SupportArticleDTO> list;
    private long total;
    private int pageNum;
    private int pageSize;
} 