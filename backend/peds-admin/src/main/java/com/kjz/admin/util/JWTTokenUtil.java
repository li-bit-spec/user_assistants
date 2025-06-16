package com.kjz.admin.util;

import com.alibaba.fastjson.JSON;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.UUID;

import com.kjz.admin.global.consts.BusinessErrorCode;
import com.kjz.common.exception.ServiceException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

/**
 * @mudule: token 生成工具
 * @author：jiangwei.li
 * @since：2021/8/30 10:08
 */
@Slf4j
public class JWTTokenUtil {

    /**
     * 私钥
     */
    public static final String JWT_SECRET = "kadingche";

    /**
     * 校验token有效性
     *
     * @param token
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String token) {
        //签名秘钥，和生成的签名的秘钥必须一样
        SecretKey key = generalKey();
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token).getBody();
        return claims;
    }


    /**
     * @return 获得token中用户信息
     */
    public static <T> T getUserInfo(String token, Class<T> c) {
        try {
            String issuer = parseJWT(token).getIssuer();
            if (StringUtils.isNotEmpty(issuer)) {
                return JSON.parseObject(issuer, c);
            }
        } catch (Exception e) {
            if (e instanceof ExpiredJwtException) {
                log.info("JWT expired, token:{}", token, e);
                throw new ServiceException(BusinessErrorCode.USER_TOKEN_FAILED); // token 失效
            }
            log.info("getUsername erro, token:{}", token, e);
            throw new ServiceException(BusinessErrorCode.PARSE_TOKEN_FAILED); // token 解析失败
        }
        return null;
    }

    /**
     * 生成签名
     *
     * @param data    用户信息
     * @param expTime 有效期 ms
     * @return 加密的token
     */
    public static <T> String sign(T data, long expTime) {
        SecretKey key = generalKey();
        String uuid = UUID.randomUUID().toString();
        long currentTime = System.currentTimeMillis();
        return Jwts.builder()
                /**唯一标识,避免冲突*/
                .setId(uuid)
                /**签名时间*/
                .setIssuedAt(new Date(currentTime))
                /**签发人信息*/
                .setIssuer(JSON.toJSONString(data))
                .setExpiration(new Date(expTime))  //签名失效时间 到期时间
                .signWith(SignatureAlgorithm.HS256, key).compact();
    }


    /**
     * 由字符串生成加密key
     *
     * @return
     */
    private static SecretKey generalKey() {
        // 本地的密码解码
        byte[] encodedKey = Base64.decodeBase64(JWT_SECRET);
        // 根据给定的字节数组使用AES加密算法构造一个密钥
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }


    public static void main(String[] args) throws Exception {




    }
}
