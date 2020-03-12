package com.zy.myblog.admin.service.impl;

import com.zy.myblog.admin.mapper.AdminMapper;
import com.zy.myblog.admin.mapper.RoleMapper;
import com.zy.myblog.admin.service.AdminService;
import com.zy.myblog.admin.service.RoleService;
import com.zy.myblog.base.service.impl.SuperServiceImpl;
import com.zy.myblog.xx.entity.Admin;
import com.zy.myblog.xx.entity.Role;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends SuperServiceImpl<RoleMapper, Role> implements RoleService {
}
