package com.kjz.common.pojo;

/**
 * @program: member
 * @description: 统一返回结果类
 * @author: zhangcheng
 * @create: 2021-07-21 01:12
 **/
import com.kjz.common.consts.code.CommonErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class CommonResult<T> implements Serializable {
    private Integer code;
    private String message;
    private Long responseId;
    private T data;

    public static <T> CommonResult<T> error(CommonResult<?> result) {
        return error(result.getCode(), result.getMessage());
    }

    public static <T> CommonResult<T> error(Integer code, String message) {
        CommonResult<T> result = new CommonResult<>();
        result.setCode(code);
        result.setMessage(message);
        result.setResponseId(System.currentTimeMillis());
        return result;
    }

    public static <T> CommonResult<T> error(Integer code, String message, T data) {
        CommonResult<T> result = new CommonResult<>();
        result.setData(data);
        result.setCode(code);
        result.setResponseId(System.currentTimeMillis());
        result.setMessage(message);
        return result;
    }

    public static <T> CommonResult<T> success(T data) {
        CommonResult<T> result = new CommonResult<>();
        result.setData(data);
        result.setCode(CommonErrorCode.SUCCESS.code());
        result.setResponseId(System.currentTimeMillis());
        result.setMessage("");
        return result;
    }

    public static <T> CommonResult<T> success(Integer code, String message, T data) {
        CommonResult<T> result = new CommonResult<>();
        result.setData(data);
        result.setCode(code);
        result.setResponseId(System.currentTimeMillis());
        result.setMessage(message);
        return result;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setResponseId(Long responseId){
        this.responseId = responseId;
    }

    public Long getResponseId(){
        return this.responseId;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return CommonErrorCode.SUCCESS.code().equals(this.code);
    }

    @JsonIgnore
    public boolean isError() {
        return !this.isSuccess();
    }

    @Override
    public String toString() {
        return "CommonResult{code=" + this.code + ", message='" + this.message + '\'' + ", data=" + this.data + '}';
    }
}
