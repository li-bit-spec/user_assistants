package com.module.service;

import com.module.entity.SupportArticle;

import java.util.List;
import java.util.Map;

/**
 * @author 李华宪
 */
public interface SupportArticleService {
    Map<String, Object> pageList(int pageNum, int pageSize, String title);
    List<SupportArticle> listAll();
    SupportArticle getById(Long id);
    void addArticle(SupportArticle article);
    void updateArticle(SupportArticle article);
    void deleteArticle(Long id);
}