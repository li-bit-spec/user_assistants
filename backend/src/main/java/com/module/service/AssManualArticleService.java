package com.module.service;

import com.module.entity.AssManualArticle;

import java.util.List;
import java.util.Map;

/**
 * @author 李华宪
 */
public interface AssManualArticleService {
    Map<String, Object> pageList(int pageNum, int pageSize, String title);
    List<AssManualArticle> listAll();
    AssManualArticle getById(Long id);
    void addArticle(AssManualArticle article);
    void updateArticle(AssManualArticle article);
    void deleteArticle(Long id);
}
