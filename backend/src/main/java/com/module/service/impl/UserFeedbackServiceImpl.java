package com.module.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.module.service.UserFeedbackService;
import com.module.mapper.UserFeedbackMapper;
import com.module.mapper.FeedbackImageMapper;
import com.module.entity.UserFeedback;
import com.module.entity.FeedbackImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserFeedbackServiceImpl implements UserFeedbackService {
    @Autowired
    private UserFeedbackMapper userFeedbackMapper;
    
    @Autowired
    private FeedbackImageMapper feedbackImageDao;

    @Override
    public Map<String, Object> pageList(int pageNum, int pageSize) {
        Page<UserFeedback> page = new Page<>(pageNum, pageSize);
        QueryWrapper<UserFeedback> wrapper = new QueryWrapper<>();
        wrapper.select("id", "content", "created_at", "updated_at")
              .orderByDesc("created_at");
        Page<UserFeedback> result = userFeedbackMapper.selectPage(page, wrapper);
        
        log.info("查询到反馈总数：{}", result.getTotal());
        
        // 为每个反馈添加图片信息
        List<Map<String, Object>> feedbackList = result.getRecords().stream().map(feedback -> {
            Map<String, Object> feedbackMap = new HashMap<>();
            feedbackMap.put("id", feedback.getId());
            feedbackMap.put("content", feedback.getContent());
            feedbackMap.put("createdAt", feedback.getCreatedAt());
            feedbackMap.put("updatedAt", feedback.getUpdatedAt());
            
            // 查询关联的图片
            QueryWrapper<FeedbackImage> imageWrapper = new QueryWrapper<>();
            imageWrapper.eq("feedback_id", feedback.getId());
            List<FeedbackImage> images = feedbackImageDao.selectList(imageWrapper);
            List<String> imageUrls = images.stream().map(FeedbackImage::getImageUrl).collect(Collectors.toList());
            feedbackMap.put("imageUrls", imageUrls);
            
            log.info("反馈ID：{}，内容：{}，图片数量：{}", feedback.getId(), feedback.getContent(), imageUrls.size());
            return feedbackMap;
        }).collect(Collectors.toList());
        
        Map<String, Object> pageResult = new HashMap<>();
        pageResult.put("list", feedbackList);
        pageResult.put("total", result.getTotal());
        pageResult.put("pageNum", (int) result.getCurrent());
        pageResult.put("pageSize", (int) result.getSize());
        
        return pageResult;
    }

    @Override
    @Transactional
    public void addFeedback(String content, List<String> imageUrls) {
        log.info("开始保存反馈，内容：{}，图片数量：{}", content, 
            imageUrls != null ? imageUrls.size() : 0);
            
        // 保存反馈内容
        UserFeedback feedback = new UserFeedback();
        feedback.setContent(content);
        userFeedbackMapper.insert(feedback);
        log.info("反馈保存成功，ID：{}", feedback.getId());
        
        // 保存图片
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String imageUrl : imageUrls) {
                FeedbackImage image = new FeedbackImage();
                image.setFeedbackId(feedback.getId());
                image.setImageUrl(imageUrl);
                feedbackImageDao.insert(image);
                log.info("图片保存成功，URL：{}", imageUrl);
            }
        }
    }

    @Override
    @Transactional
    public void deleteFeedback(Long id) {
        // 删除关联的图片
        QueryWrapper<FeedbackImage> imageWrapper = new QueryWrapper<>();
        imageWrapper.eq("feedback_id", id);
        feedbackImageDao.delete(imageWrapper);
        
        // 删除反馈
        userFeedbackMapper.deleteById(id);
    }
}