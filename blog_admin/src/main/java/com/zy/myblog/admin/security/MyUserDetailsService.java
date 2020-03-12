package com.zy.myblog.admin.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zy.myblog.admin.service.AdminService;
import com.zy.myblog.admin.service.RoleService;
import com.zy.myblog.xx.entity.Admin;
import com.zy.myblog.xx.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zy 1716457206@qq.com
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name",username);
        Admin admin = adminService.getOne(queryWrapper);
        if (admin == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            //查询出角色信息封装到admin中
            List<String> roleNames = new ArrayList<>();
            Role role = roleService.getById(admin.getRoleUid());
            roleNames.add(role.getRoleName());
            admin.setRoleNames(roleNames);
            //将admin转换为MyUserDetails
            boolean enable = admin.getStatus()==1?true:false;
            List<GrantedAuthority> authorities  =admin.getRoleNames().stream()
                    .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
             MyUserDetails myUserDetails = new MyUserDetails(
                     admin.getUid(),
                     admin.getUserName(),
                     admin.getPassWord(),
                     enable,
                     authorities
             );
            return myUserDetails;
        }
    }
}
