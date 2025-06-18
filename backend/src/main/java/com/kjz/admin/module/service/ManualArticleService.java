package com.kjz.admin.module.service;

import com.kjz.admin.module.entity.ManualArticle;

import java.util.List;
import java.util.Map;

/**
 * @author 李华宪
 */
public interface ManualArticleService {
    Map<String, Object> pageList(int pageNum, int pageSize, String title);
    List<ManualArticle> listAll();
    ManualArticle getById(Long id);
    void addArticle(ManualArticle article);
    void updateArticle(ManualArticle article);
    void deleteArticle(Long id);
}
