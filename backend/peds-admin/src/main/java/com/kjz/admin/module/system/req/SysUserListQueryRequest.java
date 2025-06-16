package com.kjz.admin.module.system.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangcheng
 */
@Data
@ApiModel("查看用户列表请求体")
public class SysUserListQueryRequest {
    /**
     * 姓名
     */
    @ApiModelProperty("姓名")
    private String username;

    /**
     * 电话
     */
    @ApiModelProperty("电话")
    private String phone;

    /**
     * 中心
     */
    @ApiModelProperty("中心id")
    private Integer centerId;

    /**
     * 页数
     */
    @ApiModelProperty("页数")
    private Integer pageNum;
    /**
     * 页数大小
     */
    @ApiModelProperty("每页大小")
    private Integer pageSize;
}
