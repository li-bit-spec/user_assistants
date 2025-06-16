package com.kjz.common.handler;

/**
 * @program: member
 * @description: 统一异常处理类
 * @author: zhangcheng
 * @create: 2021-07-21 01:18
 **/
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
//import javax.validation.ConstraintViolation;
//import javax.validation.ConstraintViolationException;

import com.kjz.common.consts.code.CommonErrorCode;
import com.kjz.common.exception.ServiceException;
import com.kjz.common.pojo.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class BaseGlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(BaseGlobalExceptionHandler.class);

    public BaseGlobalExceptionHandler() {
    }

    @ResponseBody
    @ExceptionHandler({ServiceException.class})
    public CommonResult serviceExceptionHandler(HttpServletRequest req, ServiceException ex) {
        log.error("ServiceException code={},message={}", new Object[]{ex.getCode(), ex.getMessage(), ex});
        return CommonResult.error(ex.getCode(), ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public CommonResult missingServletRequestParameterExceptionHandler(HttpServletRequest req, MissingServletRequestParameterException ex) {
        log.error("MissingServletRequestParameterException={}", ex.getMessage(), ex);
        return CommonResult.error(CommonErrorCode.MISSING_REQUEST_PARAM_ERROR.code(), CommonErrorCode.MISSING_REQUEST_PARAM_ERROR.message());
    }

//    @ResponseBody
//    @ExceptionHandler({ConstraintViolationException.class})
//    public CommonResult constraintViolationExceptionHandler(HttpServletRequest req, ConstraintViolationException ex) {
//        log.error("ConstraintViolationException={}", ex.getMessage(), ex);
//        StringBuilder detailMessage = new StringBuilder();
//
//        ConstraintViolation constraintViolation;
//        for(Iterator var4 = ex.getConstraintViolations().iterator(); var4.hasNext(); detailMessage.append(constraintViolation.getMessage())) {
//            constraintViolation = (ConstraintViolation)var4.next();
//            if (detailMessage.length() > 0) {
//                detailMessage.append(";");
//            }
//        }
//
//        return CommonResult.error(CommonErrorCode.INVALID_REQUEST_PARAM_ERROR.code(), CommonErrorCode.INVALID_REQUEST_PARAM_ERROR.message() + ":" + detailMessage.toString());
//    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public CommonResult exception(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException={}", e.getMessage(), e);
        StringBuilder detailMessage = new StringBuilder();

        ObjectError objectError;
        for(Iterator var3 = e.getBindingResult().getAllErrors().iterator(); var3.hasNext(); detailMessage.append(objectError.getDefaultMessage())) {
            objectError = (ObjectError)var3.next();
            if (detailMessage.length() > 0) {
                detailMessage.append(";");
            }
        }

        return CommonResult.error(CommonErrorCode.INVALID_REQUEST_PARAM_ERROR.code(), CommonErrorCode.INVALID_REQUEST_PARAM_ERROR.message() + ":" + detailMessage.toString());
    }

    @ResponseBody
    @ExceptionHandler({BindException.class})
    public CommonResult bindExceptionHandler(HttpServletRequest req, BindException ex) {
        log.error("BindException={}", ex.getMessage(), ex);
        StringBuilder detailMessage = new StringBuilder();

        ObjectError objectError;
        for(Iterator var4 = ex.getAllErrors().iterator(); var4.hasNext(); detailMessage.append(objectError.getDefaultMessage())) {
            objectError = (ObjectError)var4.next();
            if (detailMessage.length() > 0) {
                detailMessage.append(";");
            }
        }

        return CommonResult.error(CommonErrorCode.INVALID_REQUEST_PARAM_ERROR.code(), CommonErrorCode.INVALID_REQUEST_PARAM_ERROR.message() + ":" + detailMessage.toString());
    }

    @ResponseBody
    @ExceptionHandler({Exception.class})
    public CommonResult exceptionHandler(HttpServletRequest req, Exception e) {
        log.error("全局异常捕获器={}", e.getMessage(), e);
        return CommonResult.error(CommonErrorCode.SYS_ERROR.code(), CommonErrorCode.SYS_ERROR.message());
    }
}
