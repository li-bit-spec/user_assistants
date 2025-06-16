package com.kjz.admin.global.intercepter;

import cn.hutool.core.util.StrUtil;
import com.kjz.admin.global.annotation.Access;
import com.kjz.admin.global.consts.BusinessErrorCode;
import com.kjz.admin.util.JWTTokenUtil;
import com.kjz.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器
 */
@Slf4j
public class LoginInterceptor extends HandlerInterceptorAdapter {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**过滤指定访问路径*/
//
//        response.addHeader("last_operation_time", System.currentTimeMillis() + "");
//
//        Access methodAnnotation;
//        if (handler instanceof HandlerMethod) {
//            HandlerMethod handlerMethod = (HandlerMethod) handler;
//            methodAnnotation = handlerMethod.getMethodAnnotation(Access.class);
//
//            if (null != methodAnnotation && !methodAnnotation.isLogin()) {
//                return true;
//            } else {
//                String token = request.getHeader("token");
//                if (StrUtil.isBlank(token)) {
//                    throw new ServiceException(BusinessErrorCode.USER_UN_LOGIN); //没有token
//                }
//                /**解析jwt*/
//                UserLoginInfo userLoginInfo = JWTTokenUtil.getUserInfo(token, UserLoginInfo.class);
//                // 设置我的标识
//                request.getSession().setAttribute("system_login_user_info", userLoginInfo);
//            }
//        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }
}
