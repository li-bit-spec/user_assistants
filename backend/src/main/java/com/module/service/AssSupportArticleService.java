package com.module.service;

import com.module.entity.AssSupportArticle;

import java.util.List;
import java.util.Map;

/**
 * @author 李华宪
 */
public interface AssSupportArticleService {
    Map<String, Object> pageList(int pageNum, int pageSize, String title);
    List<AssSupportArticle> listAll();
    AssSupportArticle getById(Long id);
    void addArticle(AssSupportArticle article);
    void updateArticle(AssSupportArticle article);
    void deleteArticle(Long id);
}