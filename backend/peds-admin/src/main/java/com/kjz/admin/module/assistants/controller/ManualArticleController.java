package com.kjz.admin.module.assistants.controller;

import com.kjz.admin.module.assistants.dto.ManualArticleDTO;
import com.kjz.admin.module.assistants.req.ManualArticlePageReq;
import com.kjz.admin.module.assistants.response.ManualArticlePageResponse;
import com.kjz.admin.module.assistants.service.ManualArticleService;
import com.kjz.common.pojo.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manual")
public class ManualArticleController {
    @Autowired
    private ManualArticleService manualArticleService;

    @PostMapping("/page")
    public CommonResult<ManualArticlePageResponse> pageList(@RequestBody ManualArticlePageReq req) {
        return CommonResult.success(manualArticleService.pageList(req));
    }

    @GetMapping("/list")
    public CommonResult<List<ManualArticleDTO>> listAll() {
        return CommonResult.success(manualArticleService.listAll());
    }

    @GetMapping("/{id}")
    public CommonResult<ManualArticleDTO> getById(@PathVariable Long id) {
        return CommonResult.success(manualArticleService.getById(id));
    }

    @PostMapping("/add")
    public CommonResult<Void> add(@RequestBody ManualArticleDTO dto) {
        manualArticleService.addArticle(dto);
        return CommonResult.success(null);
    }

    @PostMapping("/update")
    public CommonResult<Void> update(@RequestBody ManualArticleDTO dto) {
        manualArticleService.updateArticle(dto);
        return CommonResult.success(null);
    }

    @DeleteMapping("/{id}")
    public CommonResult<Void> delete(@PathVariable Long id) {
        manualArticleService.deleteArticle(id);
        return CommonResult.success(null);
    }
} 