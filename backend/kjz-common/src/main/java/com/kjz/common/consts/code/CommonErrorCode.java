package com.kjz.common.consts.code;

/**
 * @program: member
 * @description: 公共异常
 * @author: zhangcheng
 * @create: 2021-07-21 01:13
 **/
public class CommonErrorCode extends AbstractCommonErrorCode {

    public static final CommonErrorCode SUCCESS = new CommonErrorCode(0, "成功");
    public static final CommonErrorCode SYSTEM_BUSY = new CommonErrorCode(10002, "系统繁忙,请稍后重试");
    public static final CommonErrorCode SYS_ERROR = new CommonErrorCode(10001, "服务端发生异常");
    public static final CommonErrorCode INVALID_REQUEST_PARAM_ERROR = new CommonErrorCode(20001, "请求参数不合法");
    public static final CommonErrorCode NOT_FOUND_REQUEST_ERROR = new CommonErrorCode(20002, "请求资源不存在");
    public static final CommonErrorCode MISSING_REQUEST_PARAM_ERROR = new CommonErrorCode(20003, "参数缺失");
    public static final CommonErrorCode FAIL_NETWORK_ERROR = new CommonErrorCode(20004, "网络异常");
    public static final CommonErrorCode ACCESS_TOO_MANY = new CommonErrorCode(20005, "访问过于频繁");
    public static final CommonErrorCode USER_AUTH_FAILED = new CommonErrorCode(30001, "用户鉴权异常");
    public static final CommonErrorCode USER_UN_LOGIN = new CommonErrorCode(30002, "用户未登录");
    public static final CommonErrorCode USER_TOKEN_FAILED = new CommonErrorCode(30003, "用户token失效");
    public static final CommonErrorCode PARSE_TOKEN_FAILED = new CommonErrorCode(30004, "解析token失败");
    public static final CommonErrorCode LOGIN_FAILED = new CommonErrorCode(30005, "登录失败");
    public static final CommonErrorCode LOGIN_PASS_FAILED = new CommonErrorCode(30006, "登录密码错误");
    public static final CommonErrorCode LOGIN_CODE_EXPIRE = new CommonErrorCode(30007, "验证码已过期");
    public static final CommonErrorCode LOGIN_CODE_FAILED = new CommonErrorCode(30008, "验证码错误");

    public CommonErrorCode(Integer code, String message) {
        super(code, message);
    }
}
