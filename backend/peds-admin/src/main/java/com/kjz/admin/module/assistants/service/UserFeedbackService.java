package com.kjz.admin.module.assistants.service;

import com.kjz.admin.module.assistants.req.UserFeedbackPageReq;
import com.kjz.admin.module.assistants.response.UserFeedbackPageResponse;
import com.kjz.admin.module.assistants.dto.UserFeedbackDTO;

public interface UserFeedbackService {
    UserFeedbackPageResponse pageList(UserFeedbackPageReq req);
    void addFeedback(UserFeedbackDTO dto);
    void deleteFeedback(Long id);
} 