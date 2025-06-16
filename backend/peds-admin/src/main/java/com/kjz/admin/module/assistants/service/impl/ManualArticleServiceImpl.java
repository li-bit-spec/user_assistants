package com.kjz.admin.module.assistants.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kjz.admin.module.assistants.dto.ManualArticleDTO;
import com.kjz.admin.module.assistants.req.ManualArticlePageReq;
import com.kjz.admin.module.assistants.response.ManualArticlePageResponse;
import com.kjz.admin.module.assistants.service.ManualArticleService;
import com.kjz.common.dao.ManualArticleDao;
import com.kjz.common.entity.po.ManualArticle;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManualArticleServiceImpl implements ManualArticleService {
    @Autowired
    private ManualArticleDao manualArticleDao;

    @Override
    public ManualArticlePageResponse pageList(ManualArticlePageReq req) {
        Page<ManualArticle> page = new Page<>(req.getPageNum(), req.getPageSize());
        QueryWrapper<ManualArticle> wrapper = new QueryWrapper<>();
        if (req.getTitle() != null && !req.getTitle().isEmpty()) {
            wrapper.like("title", req.getTitle());
        }
        wrapper.orderByDesc("created_at");
        Page<ManualArticle> result = manualArticleDao.selectPage(page, wrapper);
        List<ManualArticleDTO> dtoList = result.getRecords().stream().map(po -> {
            ManualArticleDTO dto = new ManualArticleDTO();
            BeanUtils.copyProperties(po, dto);
            return dto;
        }).collect(Collectors.toList());
        ManualArticlePageResponse resp = new ManualArticlePageResponse();
        resp.setList(dtoList);
        resp.setTotal(result.getTotal());
        resp.setPageNum((int) result.getCurrent());
        resp.setPageSize((int) result.getSize());
        return resp;
    }

    @Override
    public List<ManualArticleDTO> listAll() {
        List<ManualArticle> list = manualArticleDao.selectList(new QueryWrapper<ManualArticle>().orderByDesc("created_at"));
        return list.stream().map(po -> {
            ManualArticleDTO dto = new ManualArticleDTO();
            BeanUtils.copyProperties(po, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public ManualArticleDTO getById(Long id) {
        ManualArticle po = manualArticleDao.selectById(id);
        if (po == null) return null;
        ManualArticleDTO dto = new ManualArticleDTO();
        BeanUtils.copyProperties(po, dto);
        return dto;
    }

    @Override
    public void addArticle(ManualArticleDTO dto) {
        ManualArticle po = new ManualArticle();
        BeanUtils.copyProperties(dto, po);
        manualArticleDao.insert(po);
    }

    @Override
    public void updateArticle(ManualArticleDTO dto) {
        ManualArticle po = new ManualArticle();
        BeanUtils.copyProperties(dto, po);
        manualArticleDao.updateById(po);
    }

    @Override
    public void deleteArticle(Long id) {
        manualArticleDao.deleteById(id);
    }
} 