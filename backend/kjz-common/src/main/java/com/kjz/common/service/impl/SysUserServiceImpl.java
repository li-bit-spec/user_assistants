package com.kjz.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kjz.common.consts.Constant;
import com.kjz.common.entity.dto.SysUserQueryDTO;
import com.kjz.common.entity.po.SysUser;
import com.kjz.common.dao.SysUserDao;
import com.kjz.common.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author zhangcheng
 * @since 2022-12-11
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUser> implements SysUserService {

    @Resource
    private SysUserDao sysUserDao;

    @Override
    public Page<SysUser> list(SysUserQueryDTO systemUserQueryDTO) {
        //开始分页
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = buildQueryWrapper(systemUserQueryDTO);
        Page<SysUser> page = new Page<>();
        if (Objects.nonNull(systemUserQueryDTO.getPageSize())) {
            page.setSize(systemUserQueryDTO.getPageSize());
        }
        if (Objects.nonNull(systemUserQueryDTO.getPageNum())) {
            page.setCurrent(systemUserQueryDTO.getPageNum());
        }
        lambdaQueryWrapper.eq(SysUser::getIsDeleted, Constant.INT_ZERO);
        lambdaQueryWrapper.orderByDesc(SysUser::getUpdateTime);
        return sysUserDao.selectPage(page, lambdaQueryWrapper);
    }

    private LambdaQueryWrapper<SysUser> buildQueryWrapper(SysUserQueryDTO sysUserQueryDTO) {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = Wrappers.lambdaQuery(SysUser.class);
        if (!StringUtils.isEmpty(sysUserQueryDTO.getUsername())) {
            lambdaQueryWrapper.like(SysUser::getUsername, sysUserQueryDTO.getUsername());
        }
        if (!StringUtils.isEmpty(sysUserQueryDTO.getPhone())) {
            lambdaQueryWrapper.like(SysUser::getPhone, sysUserQueryDTO.getPhone());
        }
        if (!Objects.isNull(sysUserQueryDTO.getCenterId())) {
            lambdaQueryWrapper.eq(SysUser::getCenterId, sysUserQueryDTO.getCenterId());
        }
        return lambdaQueryWrapper;
    }
}
