package com.kjz.admin.module.assistants.controller;

import com.kjz.admin.module.assistants.dto.UserFeedbackDTO;
import com.kjz.admin.module.assistants.req.UserFeedbackPageReq;
import com.kjz.admin.module.assistants.response.UserFeedbackPageResponse;
import com.kjz.admin.module.assistants.service.UserFeedbackService;
import com.kjz.common.pojo.CommonResult;
import com.kjz.common.consts.code.CommonErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@RestController
@RequestMapping("/api/feedback")
public class UserFeedbackController {
    @Autowired
    private UserFeedbackService userFeedbackService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/page")
    public CommonResult<UserFeedbackPageResponse> pageList(@RequestBody UserFeedbackPageReq req) {
        return CommonResult.success(userFeedbackService.pageList(req));
    }

    @PostMapping("/add")
    public CommonResult<Void> add(@RequestBody UserFeedbackDTO dto) {
        try {
            log.info("接收到反馈提交请求，原始数据：{}", objectMapper.writeValueAsString(dto));
            log.info("反馈内容：{}", dto.getContent());
            log.info("图片数量：{}", dto.getImageUrls() != null ? dto.getImageUrls().size() : 0);
            if (dto.getImageUrls() != null) {
                log.info("图片URL列表：{}", String.join(", ", dto.getImageUrls()));
            }
            userFeedbackService.addFeedback(dto);
            return CommonResult.success(null);
        } catch (Exception e) {
            log.error("处理反馈提交请求失败", e);
            return CommonResult.error(CommonErrorCode.SYS_ERROR.code(), "处理请求失败");
        }
    }

    @DeleteMapping("/{id}")
    public CommonResult<Void> delete(@PathVariable Long id) {
        userFeedbackService.deleteFeedback(id);
        return CommonResult.success(null);
    }
} 