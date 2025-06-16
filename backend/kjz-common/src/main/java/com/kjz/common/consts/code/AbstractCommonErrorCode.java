package com.kjz.common.consts.code;

/**
 * @program: member
 * @description: 基础定义类
 * @author: zhangcheng
 * @create: 2021-07-21 01:09
 **/
import java.io.Serializable;

public abstract class AbstractCommonErrorCode implements Serializable {
    private final Integer code;
    private final String message;

    public AbstractCommonErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }
}
