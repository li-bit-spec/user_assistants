package com.kjz.admin.module.assistants.controller;

import com.kjz.admin.module.assistants.dto.SupportArticleDTO;
import com.kjz.admin.module.assistants.req.SupportArticlePageReq;
import com.kjz.admin.module.assistants.response.SupportArticlePageResponse;
import com.kjz.admin.module.assistants.service.SupportArticleService;
import com.kjz.common.pojo.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support")
public class SupportArticleController {
    @Autowired
    private SupportArticleService supportArticleService;

    @PostMapping("/page")
    public CommonResult<SupportArticlePageResponse> pageList(@RequestBody SupportArticlePageReq req) {
        return CommonResult.success(supportArticleService.pageList(req));
    }

    @GetMapping("/list")
    public CommonResult<List<SupportArticleDTO>> listAll() {
        return CommonResult.success(supportArticleService.listAll());
    }

    @GetMapping("/{id}")
    public CommonResult<SupportArticleDTO> getById(@PathVariable Long id) {
        return CommonResult.success(supportArticleService.getById(id));
    }

    @PostMapping("/add")
    public CommonResult<Void> add(@RequestBody SupportArticleDTO dto) {
        supportArticleService.addArticle(dto);
        return CommonResult.success(null);
    }

    @PostMapping("/update")
    public CommonResult<Void> update(@RequestBody SupportArticleDTO dto) {
        supportArticleService.updateArticle(dto);
        return CommonResult.success(null);
    }

    @DeleteMapping("/{id}")
    public CommonResult<Void> delete(@PathVariable Long id) {
        supportArticleService.deleteArticle(id);
        return CommonResult.success(null);
    }
} 