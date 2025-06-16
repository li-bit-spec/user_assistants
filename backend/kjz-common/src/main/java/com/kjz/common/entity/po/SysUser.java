package com.kjz.common.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author zhangcheng
 * @since 2022-12-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user")
public class SysUser implements Serializable {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 账号
     */
    @TableField("account")
    private String account;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 角色  1客户端用户 2服务端用户 3服务端管理员
     */
    @TableField("role")
    private Integer role;

    /**
     * 用户名称?是否和姓名一样？
     */
    @TableField("username")
    private String username;

    /**
     * 状态   1正常 0停用
     */
    @TableField("status")
    private Integer status;

    /**
     * 中心id
     */
    @TableField("center_id")
    private Integer centerId;

    /**
     * 电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 是否允许客户端登录0 否 1是
     */
    @TableField("consumer_login_flag")
    private Integer consumerLoginFlag;

    /**
     * 是否允许登陆管理后台登录0否1是
     */
    @TableField("business_login_flag")
    private Integer businessLoginFlag;

    /**
     * 上次登录时间？ b和c端都更新此时间？
     */
    @TableField("last_login_time")
    private Date lastLoginTime;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 是否删除0否1已删除
     */
    @TableField("is_deleted")
    private Integer isDeleted;


}
