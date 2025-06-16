package com.kjz.admin.module.assistants.service;

import com.kjz.admin.module.assistants.req.ManualArticlePageReq;
import com.kjz.admin.module.assistants.response.ManualArticlePageResponse;
import com.kjz.admin.module.assistants.dto.ManualArticleDTO;

import java.util.List;

public interface ManualArticleService {
    ManualArticlePageResponse pageList(ManualArticlePageReq req);
    List<ManualArticleDTO> listAll();
    ManualArticleDTO getById(Long id);
    void addArticle(ManualArticleDTO dto);
    void updateArticle(ManualArticleDTO dto);
    void deleteArticle(Long id);
}
