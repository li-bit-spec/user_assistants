package com.kjz.admin.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @mudule: ip工具
 * @author：jiangwei.li
 * @since：2021/8/31 10:08
 */
@Slf4j
public class IPUtil {

    public static final String UNKNOWN_IP = "unknown";

    private IPUtil() {
    }

    public static String getIP(HttpServletRequest request) {
        if (request == null) {
            return "0.0.0.0";
        } else {
            String xip = request.getHeader("X-Real-IP");
            String xfor = request.getHeader("X-Forwarded-For");
            if (StringUtils.isNotEmpty(xfor) && !UNKNOWN_IP.equalsIgnoreCase(xfor)) {
                int index = xfor.indexOf(",");
                return index != -1 ? xfor.substring(0, index) : xfor;
            } else {
                xfor = xip;
                if (StringUtils.isNotEmpty(xip) && !UNKNOWN_IP.equalsIgnoreCase(xip)) {
                    return xip;
                } else {
                    if (StringUtils.isBlank(xip) || UNKNOWN_IP.equalsIgnoreCase(xip)) {
                        xfor = request.getHeader("Proxy-Client-IP");
                    }

                    if (StringUtils.isBlank(xfor) || UNKNOWN_IP.equalsIgnoreCase(xfor)) {
                        xfor = request.getHeader("WL-Proxy-Client-IP");
                    }

                    if (StringUtils.isBlank(xfor) || UNKNOWN_IP.equalsIgnoreCase(xfor)) {
                        xfor = request.getHeader("HTTP_CLIENT_IP");
                    }

                    if (StringUtils.isBlank(xfor) || UNKNOWN_IP.equalsIgnoreCase(xfor)) {
                        xfor = request.getHeader("HTTP_X_FORWARDED_FOR");
                    }

                    if (StringUtils.isBlank(xfor) || UNKNOWN_IP.equalsIgnoreCase(xfor)) {
                        xfor = request.getRemoteAddr();
                    }

                    return xfor;
                }
            }
        }
    }
}
