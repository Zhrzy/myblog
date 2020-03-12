package com.zy.myblog.admin.jwt;

import com.zy.myblog.admin.security.MyUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * Jwt Token 工具类
 * @author zy 1716457206@qq.com
 */

@Component
public class JwtTokenUtil {


    //加密方式
    private final String base64Security = "MDk4ZjZiY2Q0NjIxZDM3M2NhZGU0ZTgzMjYyN2I0ZjY=";

    /**
     * //构建Token
     * @Author zy
     * @Description //构建Token
     * @param userName       名字
     * @param adminUid       uid
     * @param roleName       拥有的角色名
     * @param audience       代表这个Jwt的接受对象
     * @param issuer         代表这个Jwt的签发主题
     * @param TTLMillis      jwt有效时间
     * @return
     **/
    public String createJWT(String userName, String adminUid, String roleName,String audience,
                            String issuer, long TTLMillis) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;//签名算法
        long nowMillis = System.currentTimeMillis(); //当前时间毫秒值
        Date nowTime = new Date(nowMillis);//当前时间
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(base64Security);
        //生成签名密匙
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        //生成token
        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT")//
                .claim("adminUid", adminUid)//
                .claim("role", roleName)
                .claim("creatTime", nowTime)
                .setSubject(userName) //主题（username，用户名）
                .setIssuer(issuer) //签发者
                .setAudience(audience)
                .signWith(signatureAlgorithm, signingKey)//签名
                .setExpiration(new Date(TTLMillis+nowMillis))//token 过期时间
                .compact();
        return token;
    }

    /**
     * //解析Jwt
     * @Author zy
     * @Description
     * @Param [token, base64Security]
     * @return io.jsonwebtoken.Claims
     **/
    public Claims parseJWT(String token){
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(base64Security))
                    .parseClaimsJws(token).getBody();
            return claims;
        } catch (Exception ex) {
            return null;
        }
    }

    // 判断token是否已过期
    public boolean isExpiration(String token) {
        if (parseJWT(token) == null) { //如果解析失败肯定过期
            return true;
        } else {
            return parseJWT(token).getExpiration().before(new Date());
        }
    }

    //校验token是否合法
    public Boolean validateToken(String token, UserDetails userDetails) {
        MyUserDetails SecurityUser = (MyUserDetails) userDetails;
        final String username = getUsername(token);
        final boolean expiration = isExpiration(token);
        return (username.equals(SecurityUser.getUsername()) && !expiration);
    }


    //从token中获取用户名
    public String getUsername(String token) {
        return parseJWT(token).getSubject();
    }

    //从token中获取用户UID
    public String getUserUid(String token) {
        return parseJWT(token).get("adminUid", String.class);
    }

    //从token中获取audience
    public String getAudience(String token) {
        return parseJWT(token).getAudience();
    }

    //从token中获取issuer
    public String getIssuer(String token) {
        return parseJWT(token).getIssuer();
    }

    //从token中获取过期时间
    public Date getExpiration(String token) {
        return parseJWT(token).getExpiration();
    }

    //token是否可以更新
    public Boolean canTokenBeRefreshed(String token, String base64Security) {
        return !isExpiration(token);
    }

    //更新token
    public String refreshToken(String token,long TTLMillis) {
        String refreshToken=null;
        try {
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
            long nowMillis = System.currentTimeMillis();
            Date nowTime = new Date(nowMillis);
            //生成签名密钥
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(base64Security);
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
            final Claims claims = parseJWT(token);
            claims.put("creatDate", new Date());
            refreshToken  = Jwts.builder().setHeaderParam("typ", "JWT")
                    .setClaims(claims)
                    .setSubject(getUsername(token))
                    .setIssuer(getIssuer(token))
                    .setAudience(getAudience(token))
                    .signWith(signatureAlgorithm, signingKey)//签名;
                    .setExpiration(new Date(nowMillis + TTLMillis))
                    .compact();
        } catch (Exception e) {
            refreshToken = null;
        }
        return refreshToken;
    }



}
