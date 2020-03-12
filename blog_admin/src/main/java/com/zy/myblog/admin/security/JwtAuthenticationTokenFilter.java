package com.zy.myblog.admin.security;

import com.zy.myblog.admin.config.JwtAudienceConfig;
import com.zy.myblog.admin.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * jwt过滤器    该Filter 保证每次请求一定会过滤
 * @author zy 1716457206@qq.com
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtAudienceConfig audienceConfig;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /*三个jwt配置*/
    @Value(value = "${tokenHead}")
    private String tokenHead;  //bearer
    @Value(value = "${tokenHeader}")
    private String tokenHeader; //Authorization
    @Value(value = "${jwtaudience.expiresSecond}")
    private Long expiresSecond;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //得到请求头信息authorization信息
        String authHeader = request.getHeader(tokenHeader);
        System.out.println("token为："+authHeader);
        //请求头 'Authorization': tokenHead + token
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            final String token = authHeader.substring(tokenHead.length());
            //判断token是否过期
            if (!jwtTokenUtil.isExpiration(token)) {
                //刷新token过期时间
                jwtTokenUtil.refreshToken(token, expiresSecond);
                System.out.println("token未过期，刷新token");
            } else {
                filterChain.doFilter(request, response);
                return;
            }
            String username = jwtTokenUtil.getUsername(token);
            String adminUid = jwtTokenUtil.getUserUid(token);
            String rolename = (String) jwtTokenUtil.parseJWT(token).get("role");
            //把adminUid存储到request中
            request.setAttribute("adminUid", adminUid);
           // logger.info("解析出来用户 : " + username);
           //logger.info("解析出来的用户Uid : " + adminUid);
            System.out.println("解析出来用户 : " + username);
            System.out.println("解析出来角色 : " + rolename);
            System.out.println("filter中adminUid"+adminUid);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.myUserDetailsService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(
                            request));
                    logger.info("authenticated user " + username + ", setting security context");
                    SecurityContextHolder.getContext().setAuthentication(authentication);//以后可以security中取得SecurityUser信息
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
