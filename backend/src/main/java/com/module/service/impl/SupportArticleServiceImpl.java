package com.module.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.module.entity.SupportArticle;
import com.module.mapper.SupportArticleMapper;
import com.module.service.SupportArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 李华宪
 */
@Service
public class SupportArticleServiceImpl implements SupportArticleService {
    @Autowired
    private SupportArticleMapper supportArticleMapper;

    @Override
    public Map<String, Object> pageList(int pageNum, int pageSize, String title) {
        Page<SupportArticle> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SupportArticle> wrapper = new QueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like("title", title);
        }
        wrapper.orderByDesc("created_at");
        Page<SupportArticle> result = supportArticleMapper.selectPage(page, wrapper);
        
        Map<String, Object> pageResult = new HashMap<>();
        pageResult.put("list", result.getRecords());
        pageResult.put("total", result.getTotal());
        pageResult.put("pageNum", (int) result.getCurrent());
        pageResult.put("pageSize", (int) result.getSize());
        return pageResult;
    }

    @Override
    public List<SupportArticle> listAll() {
        return supportArticleMapper.selectList(new QueryWrapper<SupportArticle>().orderByDesc("created_at"));
    }

    @Override
    public SupportArticle getById(Long id) {
        return supportArticleMapper.selectById(id);
    }

    @Override
    public void addArticle(SupportArticle article) {
        supportArticleMapper.insert(article);
    }

    @Override
    public void updateArticle(SupportArticle article) {
        supportArticleMapper.updateById(article);
    }

    @Override
    public void deleteArticle(Long id) {
        supportArticleMapper.deleteById(id);
    }
}