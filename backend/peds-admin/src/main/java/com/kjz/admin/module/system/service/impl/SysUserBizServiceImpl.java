package com.kjz.admin.module.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kjz.admin.module.system.dto.SysUserAddDTO;
import com.kjz.admin.module.system.dto.SysUserListQueryDTO;
import com.kjz.admin.module.system.dto.SysUserModifyDTO;
import com.kjz.admin.module.system.response.SysUserListResponse;
import com.kjz.admin.module.system.service.SysUserBizService;
import com.kjz.common.entity.dto.SysUserQueryDTO;
import com.kjz.common.entity.po.PageInfo;
import com.kjz.common.entity.po.SysUser;
import com.kjz.common.service.SysUserService;
import com.kjz.common.util.PageInfoUtil;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhangcheng
 * 2022-12-11 17:40:39
 */
@Service
public class SysUserBizServiceImpl implements SysUserBizService {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private PageInfoUtil pageInfoUtil;

    @Resource
    private MapperFacade mapperFacade;

    @Override
    public PageInfo<SysUserListResponse> list(SysUserListQueryDTO sysUserListQueryDTO) {

        SysUserQueryDTO sysUserQueryDTO = mapperFacade.map(sysUserListQueryDTO, SysUserQueryDTO.class);
        Page<SysUser> userPageList = sysUserService.list(sysUserQueryDTO);
        return pageInfoUtil.convert(userPageList, SysUserListResponse.class);

    }

    @Override
    public SysUser detail(Long id) {
        return null;
    }

    @Override
    public Boolean add(SysUserAddDTO sysUserAddDTO) {
        return null;
    }

    @Override
    public Boolean modify(SysUserModifyDTO sysUserModifyDTO) {
        return null;
    }

    @Override
    public Boolean deleteById(Long id) {
        return null;
    }
}
