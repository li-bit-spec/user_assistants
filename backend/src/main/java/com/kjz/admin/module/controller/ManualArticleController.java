package com.kjz.admin.module.controller;

import com.kjz.admin.module.entity.ManualArticle;
import com.kjz.admin.module.service.ManualArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 李华宪
 */
@RestController
@RequestMapping("/api/manual")
public class ManualArticleController {
    @Autowired
    private ManualArticleService manualArticleService;

    @PostMapping("/page")
    public Map<String, Object> pageList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String title) {
        Map<String, Object> result = manualArticleService.pageList(pageNum, pageSize, title);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", result);
        return response;
    }

    @GetMapping("/list")
    public Map<String, Object> listAll() {
        List<ManualArticle> list = manualArticleService.listAll();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", list);
        return response;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        ManualArticle article = manualArticleService.getById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", article);
        return response;
    }

    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody ManualArticle article) {
        manualArticleService.addArticle(article);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", null);
        return response;
    }

    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody ManualArticle article) {
        manualArticleService.updateArticle(article);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", null);
        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        manualArticleService.deleteArticle(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", null);
        return response;
    }
} 