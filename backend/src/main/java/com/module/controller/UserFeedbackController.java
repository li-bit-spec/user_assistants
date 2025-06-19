package com.module.controller;

import com.module.service.AssUserFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 李华宪
 */
@Slf4j
@RestController
@RequestMapping("/api/feedback")
public class UserFeedbackController {
    @Autowired
    private AssUserFeedbackService assUserFeedbackService;

    @PostMapping("/page")
    public Map<String, Object> pageList(@RequestBody Map<String, Object> pageRequest) {
        // 从请求体中获取分页参数，并设置默认值
        // 处理数据类型转换，前端可能传递Number类型
        int pageNum = 1;
        int pageSize = 10;
        
        if (pageRequest.get("pageNum") != null) {
            Object pageNumObj = pageRequest.get("pageNum");
            pageNum = pageNumObj instanceof Number ? 
                ((Number) pageNumObj).intValue() : 
                Integer.parseInt(pageNumObj.toString());
        }
        
        if (pageRequest.get("pageSize") != null) {
            Object pageSizeObj = pageRequest.get("pageSize");
            pageSize = pageSizeObj instanceof Number ? 
                ((Number) pageSizeObj).intValue() : 
                Integer.parseInt(pageSizeObj.toString());
        }
        
        log.info("接收到分页请求，页码：{}，每页大小：{}", pageNum, pageSize);
        
        Map<String, Object> result = assUserFeedbackService.pageList(pageNum, pageSize);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", result);
        
        log.info("返回分页结果，总数：{}", result.get("total"));
        return response;
    }

    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody Map<String, Object> feedbackData) {
        Map<String, Object> response = new HashMap<>();
        try {
            String content = (String) feedbackData.get("content");
            List<String> imageUrls = (List<String>) feedbackData.get("imageUrls");
            
            log.info("接收到反馈提交请求，内容：{}", content);
            log.info("图片数量：{}", imageUrls != null ? imageUrls.size() : 0);
            
            assUserFeedbackService.addFeedback(content, imageUrls);
            response.put("code", 0);
            response.put("message", "success");
            response.put("data", null);
        } catch (Exception e) {
            log.error("处理反馈提交请求失败", e);
            response.put("code", 1);
            response.put("message", "处理请求失败：" + e.getMessage());
            response.put("data", null);
        }
        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        assUserFeedbackService.deleteFeedback(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", null);
        return response;
    }
}
