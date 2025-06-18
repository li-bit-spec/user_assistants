package com.module.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.module.entity.AssManualArticle;
import com.module.service.AssManualArticleService;
import com.module.mapper.AssManualArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 李华宪
 */
@Service
public class AssManualArticleServiceImpl implements AssManualArticleService {
    @Autowired
    private AssManualArticleMapper assmanualArticleMapper;

    @Override
    public Map<String, Object> pageList(int pageNum, int pageSize, String title) {
        Page<AssManualArticle> page = new Page<>(pageNum, pageSize);
        QueryWrapper<AssManualArticle> wrapper = new QueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like("title", title);
        }
        wrapper.orderByDesc("created_at");
        Page<AssManualArticle> result = assmanualArticleMapper.selectPage(page, wrapper);
        
        Map<String, Object> pageResult = new HashMap<>();
        pageResult.put("list", result.getRecords());
        pageResult.put("total", result.getTotal());
        pageResult.put("pageNum", (int) result.getCurrent());
        pageResult.put("pageSize", (int) result.getSize());
        return pageResult;
    }

    @Override
    public List<AssManualArticle> listAll() {
        return assmanualArticleMapper.selectList(new QueryWrapper<AssManualArticle>().orderByDesc("created_at"));
    }

    @Override
    public AssManualArticle getById(Long id) {
        return assmanualArticleMapper.selectById(id);
    }

    @Override
    public void addArticle(AssManualArticle article) {
        assmanualArticleMapper.insert(article);
    }

    @Override
    public void updateArticle(AssManualArticle article) {
        assmanualArticleMapper.updateById(article);
    }

    @Override
    public void deleteArticle(Long id) {
        assmanualArticleMapper.deleteById(id);
    }
}
