package com.kjz.admin.module.service;

import java.util.List;
import java.util.Map;

public interface UserFeedbackService {
    Map<String, Object> pageList(int pageNum, int pageSize);
    void addFeedback(String content, List<String> imageUrls);
    void deleteFeedback(Long id);
} 