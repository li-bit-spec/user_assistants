package com.kjz.admin.module.assistants.response;

import com.kjz.admin.module.assistants.dto.UserFeedbackDTO;
import lombok.Data;
import java.util.List;

@Data
public class UserFeedbackPageResponse {
    private List<UserFeedbackDTO> list;
    private long total;
    private int pageNum;
    private int pageSize;
} 