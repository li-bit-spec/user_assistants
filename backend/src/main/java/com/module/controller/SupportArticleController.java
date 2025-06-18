package com.module.controller;

import com.module.entity.SupportArticle;
import com.module.service.SupportArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 李华宪
 */
@RestController
@RequestMapping("/api/support")
public class SupportArticleController {
    @Autowired
    private SupportArticleService supportArticleService;

    @PostMapping("/page")
    public Map<String, Object> pageList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String title) {
        Map<String, Object> result = supportArticleService.pageList(pageNum, pageSize, title);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", result);
        return response;
    }

    @GetMapping("/list")
    public Map<String, Object> listAll() {
        List<SupportArticle> list = supportArticleService.listAll();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", list);
        return response;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        SupportArticle article = supportArticleService.getById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", article);
        return response;
    }

    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody SupportArticle article) {
        supportArticleService.addArticle(article);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", null);
        return response;
    }

    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody SupportArticle article) {
        supportArticleService.updateArticle(article);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", null);
        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        supportArticleService.deleteArticle(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", null);
        return response;
    }
}