package com.kjz.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kjz.common.entity.po.UserFeedback;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserFeedbackDao extends BaseMapper<UserFeedback> {
} 