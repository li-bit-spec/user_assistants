package com.kjz.admin.module.assistants.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kjz.admin.module.assistants.dto.UserFeedbackDTO;
import com.kjz.admin.module.assistants.req.UserFeedbackPageReq;
import com.kjz.admin.module.assistants.response.UserFeedbackPageResponse;
import com.kjz.admin.module.assistants.service.UserFeedbackService;
import com.kjz.common.dao.UserFeedbackDao;
import com.kjz.common.dao.FeedbackImageDao;
import com.kjz.common.entity.po.UserFeedback;
import com.kjz.common.entity.po.FeedbackImage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserFeedbackServiceImpl implements UserFeedbackService {
    @Autowired
    private UserFeedbackDao userFeedbackDao;
    
    @Autowired
    private FeedbackImageDao feedbackImageDao;

    @Override
    public UserFeedbackPageResponse pageList(UserFeedbackPageReq req) {
        Page<UserFeedback> page = new Page<>(req.getPageNum(), req.getPageSize());
        QueryWrapper<UserFeedback> wrapper = new QueryWrapper<>();
        wrapper.select("id", "content", "created_at", "updated_at")
              .orderByDesc("created_at");
        Page<UserFeedback> result = userFeedbackDao.selectPage(page, wrapper);
        
        log.info("查询到反馈总数：{}", result.getTotal());
        log.info("当前页数据：{}", result.getRecords());
        
        List<UserFeedbackDTO> dtoList = result.getRecords().stream().map(po -> {
            UserFeedbackDTO dto = new UserFeedbackDTO();
            BeanUtils.copyProperties(po, dto);
            
            // 查询关联的图片
            QueryWrapper<FeedbackImage> imageWrapper = new QueryWrapper<>();
            imageWrapper.eq("feedback_id", po.getId());
            List<FeedbackImage> images = feedbackImageDao.selectList(imageWrapper);
            List<String> imageUrls = images.stream().map(FeedbackImage::getImageUrl).collect(Collectors.toList());
            dto.setImageUrls(imageUrls);
            
            log.info("反馈ID：{}，内容：{}，图片数量：{}", po.getId(), po.getContent(), imageUrls.size());
            if (!imageUrls.isEmpty()) {
                log.info("图片URL列表：{}", String.join(", ", imageUrls));
            }
            return dto;
        }).collect(Collectors.toList());
        
        UserFeedbackPageResponse resp = new UserFeedbackPageResponse();
        resp.setList(dtoList);
        resp.setTotal(result.getTotal());
        resp.setPageNum((int) result.getCurrent());
        resp.setPageSize((int) result.getSize());
        
        log.info("返回分页数据：{}", resp);
        return resp;
    }

    @Override
    @Transactional
    public void addFeedback(UserFeedbackDTO dto) {
        log.info("开始保存反馈，内容：{}，图片数量：{}", dto.getContent(), 
            dto.getImageUrls() != null ? dto.getImageUrls().size() : 0);
            
        // 保存反馈内容
        UserFeedback po = new UserFeedback();
        BeanUtils.copyProperties(dto, po);
        userFeedbackDao.insert(po);
        log.info("反馈保存成功，ID：{}", po.getId());
        
        // 保存图片
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            for (String imageUrl : dto.getImageUrls()) {
                FeedbackImage image = new FeedbackImage();
                image.setFeedbackId(po.getId());
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
        userFeedbackDao.deleteById(id);
    }
} 