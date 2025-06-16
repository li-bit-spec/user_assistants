package com.kjz.common.entity.dto;

import lombok.Data;

/**
 * @author zhangcheng
 */
@Data
public class SysUserQueryDTO {
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
