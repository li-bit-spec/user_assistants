package com.kjz.admin.module.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kjz.admin.module.entity.SupportArticle;
import com.kjz.admin.module.service.SupportArticleService;
import com.kjz.admin.module.dao.SupportArticleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SupportArticleServiceImpl implements SupportArticleService {
    @Autowired
    private SupportArticleDao supportArticleDao;

    @Override
    public Map<String, Object> pageList(int pageNum, int pageSize, String title) {
        Page<SupportArticle> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SupportArticle> wrapper = new QueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like("title", title);
        }
        wrapper.orderByDesc("created_at");
        Page<SupportArticle> result = supportArticleDao.selectPage(page, wrapper);
        
        Map<String, Object> pageResult = new HashMap<>();
        pageResult.put("list", result.getRecords());
        pageResult.put("total", result.getTotal());
        pageResult.put("pageNum", (int) result.getCurrent());
        pageResult.put("pageSize", (int) result.getSize());
        return pageResult;
    }

    @Override
    public List<SupportArticle> listAll() {
        return supportArticleDao.selectList(new QueryWrapper<SupportArticle>().orderByDesc("created_at"));
    }

    @Override
    public SupportArticle getById(Long id) {
        return supportArticleDao.selectById(id);
    }

    @Override
    public void addArticle(SupportArticle article) {
        supportArticleDao.insert(article);
    }

    @Override
    public void updateArticle(SupportArticle article) {
        supportArticleDao.updateById(article);
    }

    @Override
    public void deleteArticle(Long id) {
        supportArticleDao.deleteById(id);
    }
} 