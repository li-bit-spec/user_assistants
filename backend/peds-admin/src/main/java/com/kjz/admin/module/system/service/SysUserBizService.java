package com.kjz.admin.module.system.service;

import com.kjz.admin.module.system.dto.SysUserAddDTO;
import com.kjz.admin.module.system.dto.SysUserListQueryDTO;
import com.kjz.admin.module.system.dto.SysUserModifyDTO;
import com.kjz.admin.module.system.response.SysUserListResponse;
import com.kjz.common.entity.po.PageInfo;
import com.kjz.common.entity.po.SysUser;

/**
 * @author zhangcheng
 * 2022-12-11 17:39:35
 */
public interface SysUserBizService {

    /**
     * 查询用户列表
     *
     * @param sysUserListQueryDTO
     * @return
     */
    PageInfo<SysUserListResponse> list(SysUserListQueryDTO sysUserListQueryDTO);

    /**
     * 查询用户详情
     *
     * @param id
     * @return
     */
    SysUser detail(Long id);

    /**
     * 添加新用户
     *
     * @param sysUserAddDTO
     * @return
     */
    Boolean add(SysUserAddDTO sysUserAddDTO);


    /**
     * 修改用户
     *
     * @param sysUserModifyDTO
     * @return
     */
    Boolean modify(SysUserModifyDTO sysUserModifyDTO);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    Boolean deleteById(Long id);
}
