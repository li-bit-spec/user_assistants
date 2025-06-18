package com.module.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.module.entity.AssSupportArticle;
import com.module.mapper.AssSupportArticleMapper;
import com.module.service.AssSupportArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 李华宪
 */
@Service
public class AssSupportArticleServiceImpl implements AssSupportArticleService {
    @Autowired
    private AssSupportArticleMapper assSupportArticleMapper;

    @Override
    public Map<String, Object> pageList(int pageNum, int pageSize, String title) {
        Page<AssSupportArticle> page = new Page<>(pageNum, pageSize);
        QueryWrapper<AssSupportArticle> wrapper = new QueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like("title", title);
        }
        wrapper.orderByDesc("created_at");
        Page<AssSupportArticle> result = assSupportArticleMapper.selectPage(page, wrapper);
        
        Map<String, Object> pageResult = new HashMap<>();
        pageResult.put("list", result.getRecords());
        pageResult.put("total", result.getTotal());
        pageResult.put("pageNum", (int) result.getCurrent());
        pageResult.put("pageSize", (int) result.getSize());
        return pageResult;
    }

    @Override
    public List<AssSupportArticle> listAll() {
        return assSupportArticleMapper.selectList(new QueryWrapper<AssSupportArticle>().orderByDesc("created_at"));
    }

    @Override
    public AssSupportArticle getById(Long id) {
        return assSupportArticleMapper.selectById(id);
    }

    @Override
    public void addArticle(AssSupportArticle article) {
        assSupportArticleMapper.insert(article);
    }

    @Override
    public void updateArticle(AssSupportArticle article) {
        assSupportArticleMapper.updateById(article);
    }

    @Override
    public void deleteArticle(Long id) {
        assSupportArticleMapper.deleteById(id);
    }
}