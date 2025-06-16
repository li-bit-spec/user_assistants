package com.kjz.common.exception;

import com.kjz.common.consts.code.AbstractCommonErrorCode;
import com.kjz.common.pojo.CommonResult;

/**
 * @program: member
 * @description: 自定义业务异常
 * @author: zhangcheng
 * @create: 2021-07-21 01:07
 **/
public final class ServiceException extends RuntimeException {
    private final Integer code;

    public ServiceException(AbstractCommonErrorCode errorCode) {
        super(errorCode.message());
        this.code = errorCode.code();
    }

    public ServiceException(AbstractCommonErrorCode abstractCommonErrorCode, String message) {
        super(message);
        this.code = abstractCommonErrorCode.code();
    }

    public ServiceException(CommonResult result) {
        super(result.getMessage());
        this.code = result.getCode();
    }

    public Integer getCode() {
        return this.code;
    }
}

