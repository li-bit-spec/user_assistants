package com.kjz.admin.module.system.controller;


import com.kjz.admin.module.system.dto.SysUserListQueryDTO;
import com.kjz.admin.module.system.req.SysUserListQueryRequest;
import com.kjz.admin.module.system.response.SysUserListResponse;
import com.kjz.admin.module.system.service.SysUserBizService;
import com.kjz.common.entity.po.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @description: 用户相关controller
 * @author: zhangcheng
 * @create: 2021-07-07 15:50
 **/
@RestController
@RequestMapping("/system/user")
@Api(tags = "用户管理")
public class SysUserController {

    @Resource
    private SysUserBizService sysUserBizService;

    @Resource
    private MapperFacade mapperFacade;

    /**
     * 查询用户列表
     *
     * @param sysUserListQueryRequest
     * @return
     */
    @PostMapping(value = "/v1/list")
    @ApiOperation(value = "用户列表")
    public PageInfo<SysUserListResponse> userList(@RequestBody @Validated SysUserListQueryRequest sysUserListQueryRequest) {
        SysUserListQueryDTO systemUserQueryDTO = mapperFacade.map(sysUserListQueryRequest, SysUserListQueryDTO.class);
        return sysUserBizService.list(systemUserQueryDTO);
    }
}
