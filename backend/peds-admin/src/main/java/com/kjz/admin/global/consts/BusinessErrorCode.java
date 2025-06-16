package com.kjz.admin.global.consts;

import com.kjz.common.consts.code.CommonErrorCode;

/**
 * @program: member
 * @description: 后台统一错误码
 * @author: zhangcheng
 * @create: 2021-07-21 09:08
 **/
public class BusinessErrorCode extends CommonErrorCode {
    private static final String ERROR_MESSAGE = "服务异常，请重新操作或联系客服";
    public static final CommonErrorCode DATA_NOT_EXISTS = new CommonErrorCode(100001, "数据不存在");
    public static final CommonErrorCode DATA_EXISTS = new CommonErrorCode(100002, "数据已存在");
    public static final CommonErrorCode SAVE_FAIL = new CommonErrorCode(101001, "保存数据失败");
    public static final CommonErrorCode DELETE_FAIL = new CommonErrorCode(101002, "删除数据失败");
    public static final CommonErrorCode USER_NOT_EXISTS = new CommonErrorCode(101003, "用户信息不存在");
    public static final CommonErrorCode USER_HAS_EXISTS = new CommonErrorCode(101004, "用户账户重复");

    public static final CommonErrorCode FILE_NOT_EXISTS = new CommonErrorCode(102001, "上传文件不存在");
    public static final CommonErrorCode FILE_UPLOAD_FAIL = new CommonErrorCode(102002, "上传文件失败");

    public static final CommonErrorCode ROLE_HAS_EXISTS = new CommonErrorCode(103001, "角色名称重复");
    public static final CommonErrorCode ROLE_NOT_EXISTS = new CommonErrorCode(103002, "角色不存在");

    public static final CommonErrorCode PERMISSION_NOT_EXISTS = new CommonErrorCode(104001, "权限菜单不存在");
    public static final CommonErrorCode PERMISSION_HAS_EXISTS = new CommonErrorCode(104002, "权限菜单名称重复");

    public static final CommonErrorCode TIME_RANGE_FALSE = new CommonErrorCode(114002, "最长不能超过1个月");




    public BusinessErrorCode(Integer code, String message) {
        super(code, message);
    }
}
