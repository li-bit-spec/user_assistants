package com.kjz.admin.module.assistants.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kjz.admin.module.assistants.dto.SupportArticleDTO;
import com.kjz.admin.module.assistants.req.SupportArticlePageReq;
import com.kjz.admin.module.assistants.response.SupportArticlePageResponse;
import com.kjz.admin.module.assistants.service.SupportArticleService;
import com.kjz.common.dao.SupportArticleDao;
import com.kjz.common.entity.po.SupportArticle;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupportArticleServiceImpl implements SupportArticleService {
    @Autowired
    private SupportArticleDao supportArticleDao;

    @Override
    public SupportArticlePageResponse pageList(SupportArticlePageReq req) {
        Page<SupportArticle> page = new Page<>(req.getPageNum(), req.getPageSize());
        QueryWrapper<SupportArticle> wrapper = new QueryWrapper<>();
        if (req.getTitle() != null && !req.getTitle().isEmpty()) {
            wrapper.like("title", req.getTitle());
        }
        wrapper.orderByDesc("created_at");
        Page<SupportArticle> result = supportArticleDao.selectPage(page, wrapper);
        List<SupportArticleDTO> dtoList = result.getRecords().stream().map(po -> {
            SupportArticleDTO dto = new SupportArticleDTO();
            BeanUtils.copyProperties(po, dto);
            return dto;
        }).collect(Collectors.toList());
        SupportArticlePageResponse resp = new SupportArticlePageResponse();
        resp.setList(dtoList);
        resp.setTotal(result.getTotal());
        resp.setPageNum((int) result.getCurrent());
        resp.setPageSize((int) result.getSize());
        return resp;
    }

    @Override
    public List<SupportArticleDTO> listAll() {
        List<SupportArticle> list = supportArticleDao.selectList(new QueryWrapper<SupportArticle>().orderByDesc("created_at"));
        return list.stream().map(po -> {
            SupportArticleDTO dto = new SupportArticleDTO();
            BeanUtils.copyProperties(po, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public SupportArticleDTO getById(Long id) {
        SupportArticle po = supportArticleDao.selectById(id);
        if (po == null) return null;
        SupportArticleDTO dto = new SupportArticleDTO();
        BeanUtils.copyProperties(po, dto);
        return dto;
    }

    @Override
    public void addArticle(SupportArticleDTO dto) {
        SupportArticle po = new SupportArticle();
        BeanUtils.copyProperties(dto, po);
        supportArticleDao.insert(po);
    }

    @Override
    public void updateArticle(SupportArticleDTO dto) {
        SupportArticle po = new SupportArticle();
        BeanUtils.copyProperties(dto, po);
        supportArticleDao.updateById(po);
    }

    @Override
    public void deleteArticle(Long id) {
        supportArticleDao.deleteById(id);
    }
} 