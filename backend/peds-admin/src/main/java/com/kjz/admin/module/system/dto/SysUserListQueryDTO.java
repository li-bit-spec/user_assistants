package com.kjz.admin.module.system.dto;

import lombok.Data;

/**
 * @program: lmember-member
 * @description: 查询条件
 * @author: zhangcheng
 * @create: 2021-07-07 16:36
 **/
@Data
public class SysUserListQueryDTO {


    /**
     * 姓名
     */
    private String username;

    /**
     * 电话
     */
    private String phone;

    /**
     * 中心
     */
    private Integer centerId;

    /**
     * 页数
     */
    private Integer pageNum;
    /**
     * 页数大小
     */
    private Integer pageSize;
}
