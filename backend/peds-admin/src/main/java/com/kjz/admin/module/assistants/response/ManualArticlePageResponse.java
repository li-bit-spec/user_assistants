package com.kjz.admin.module.assistants.response;

import com.kjz.admin.module.assistants.dto.ManualArticleDTO;
import lombok.Data;
import java.util.List;

@Data
public class ManualArticlePageResponse {
    private List<ManualArticleDTO> list;
    private long total;
    private int pageNum;
    private int pageSize;
} 