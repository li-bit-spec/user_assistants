package com.module.service;

import java.util.List;
import java.util.Map;

/**
 * @author 李华宪
 */
public interface AssUserFeedbackService {
    Map<String, Object> pageList(int pageNum, int pageSize);
    void addFeedback(String content, List<String> imageUrls);
    void deleteFeedback(Long id);
}
