package com.kjz.admin.module.assistants.service;

import com.kjz.admin.module.assistants.req.SupportArticlePageReq;
import com.kjz.admin.module.assistants.response.SupportArticlePageResponse;
import com.kjz.admin.module.assistants.dto.SupportArticleDTO;

import java.util.List;

public interface SupportArticleService {
    SupportArticlePageResponse pageList(SupportArticlePageReq req);
    List<SupportArticleDTO> listAll();
    SupportArticleDTO getById(Long id);
    void addArticle(SupportArticleDTO dto);
    void updateArticle(SupportArticleDTO dto);
    void deleteArticle(Long id);
} 