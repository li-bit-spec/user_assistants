package com.kjz.common.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author zhangcheng
 * @since 2022-12-11
 */
@Data
public class SysUserDTO implements Serializable {


    private Integer id;

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 角色  1客户端用户 2服务端用户 3服务端管理员
     */
    private Integer role;

    /**
     * 用户名称?是否和姓名一样？
     */
    private String username;

    /**
     * 状态   1正常 0停用
     */
    private Integer status;

    /**
     * 中心id
     */
    private Integer centerId;

    /**
     * 电话
     */
    private String phone;

    /**
     * 是否允许客户端登录0 否 1是
     */
    private Integer consumerLoginFlag;

    /**
     * 是否允许登陆管理后台登录0否1是
     */
    private Integer businessLoginFlag;

    /**
     * 上次登录时间？ b和c端都更新此时间？
     */
    private Date lastLoginTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除0否1已删除
     */
    private Integer isDeleted;


}
