package com.zy.myblog.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 相关配置，用于创建jwt token
 * @author zy 1716457206@qq.com
 */
@ConfigurationProperties(prefix = "jwtaudience")
@Component
@Data
public class JwtAudienceConfig {
    private String clientId;//客户端id
    private String name;//客户端名
    private int expiresSecond;//过期时间
}
