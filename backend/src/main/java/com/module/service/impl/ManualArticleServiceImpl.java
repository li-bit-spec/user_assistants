package com.module.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.module.entity.ManualArticle;
import com.module.service.ManualArticleService;
import com.module.mapper.ManualArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 李华宪
 */
@Service
public class ManualArticleServiceImpl implements ManualArticleService {
    @Autowired
    private ManualArticleMapper manualArticleMapper;

    @Override
    public Map<String, Object> pageList(int pageNum, int pageSize, String title) {
        Page<ManualArticle> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ManualArticle> wrapper = new QueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like("title", title);
        }
        wrapper.orderByDesc("created_at");
        Page<ManualArticle> result = manualArticleMapper.selectPage(page, wrapper);
        
        Map<String, Object> pageResult = new HashMap<>();
        pageResult.put("list", result.getRecords());
        pageResult.put("total", result.getTotal());
        pageResult.put("pageNum", (int) result.getCurrent());
        pageResult.put("pageSize", (int) result.getSize());
        return pageResult;
    }

    @Override
    public List<ManualArticle> listAll() {
        return manualArticleMapper.selectList(new QueryWrapper<ManualArticle>().orderByDesc("created_at"));
    }

    @Override
    public ManualArticle getById(Long id) {
        return manualArticleMapper.selectById(id);
    }

    @Override
    public void addArticle(ManualArticle article) {
        manualArticleMapper.insert(article);
    }

    @Override
    public void updateArticle(ManualArticle article) {
        manualArticleMapper.updateById(article);
    }

    @Override
    public void deleteArticle(Long id) {
        manualArticleMapper.deleteById(id);
    }
}
