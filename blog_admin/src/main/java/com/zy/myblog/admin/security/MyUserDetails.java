package com.zy.myblog.admin.security;

import com.sun.org.apache.bcel.internal.generic.DDIV;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * 用户信息
 * @author zy 1716457206@qq.com
 */
@Data
public class MyUserDetails implements UserDetails {

    private static final long serialVersionUID = 1;
    private final String uid;
    private final String username;
    private final String password;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    //构造方法
    public MyUserDetails(String uid,String username, String password, boolean enabled,
            Collection<? extends GrantedAuthority> authorities)
    {
        this.uid = uid;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.authorities = authorities;
    }



    //以下为覆写方法
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // 账户是否未过期
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    // 账户是否未锁定
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 密码是否未过期
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 账户是否激活
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
