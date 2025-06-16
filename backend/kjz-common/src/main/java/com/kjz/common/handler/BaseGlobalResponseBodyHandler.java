package com.kjz.common.handler;

import com.alibaba.fastjson.JSON;
import com.kjz.common.pojo.CommonResult;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author zhangcheng
 */
@ControllerAdvice(basePackages = "com.kjz")
public class BaseGlobalResponseBodyHandler implements ResponseBodyAdvice {

    public BaseGlobalResponseBodyHandler() {
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass, org.springframework.http.server.ServerHttpRequest serverHttpRequest, org.springframework.http.server.ServerHttpResponse serverHttpResponse) {
        if (body instanceof CommonResult) {
            return body;
        } else {
            return body instanceof String ? JSON.toJSONString(CommonResult.success(body)) : CommonResult.success(body);
        }
    }
}
