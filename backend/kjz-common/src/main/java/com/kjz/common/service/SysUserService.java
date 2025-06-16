package com.kjz.common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kjz.common.entity.dto.SysUserQueryDTO;
import com.kjz.common.entity.po.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author zhangcheng
 * @since 2022-12-11
 */
public interface SysUserService extends IService<SysUser> {
    /**
     *  查询用户列表
     * @param systemUserQueryDTO
     * @return
     */
    Page<SysUser> list(SysUserQueryDTO systemUserQueryDTO);
}
