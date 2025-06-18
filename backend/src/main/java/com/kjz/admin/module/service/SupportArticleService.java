package com.kjz.admin.module.service;

import com.kjz.admin.module.entity.SupportArticle;

import java.util.List;
import java.util.Map;

public interface SupportArticleService {
    Map<String, Object> pageList(int pageNum, int pageSize, String title);
    List<SupportArticle> listAll();
    SupportArticle getById(Long id);
    void addArticle(SupportArticle article);
    void updateArticle(SupportArticle article);
    void deleteArticle(Long id);
} 