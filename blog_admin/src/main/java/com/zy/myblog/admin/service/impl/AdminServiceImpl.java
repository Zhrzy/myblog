package com.zy.myblog.admin.service.impl;

import com.zy.myblog.admin.mapper.AdminMapper;
import com.zy.myblog.admin.service.AdminService;
import com.zy.myblog.base.service.impl.SuperServiceImpl;
import com.zy.myblog.xx.entity.Admin;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends SuperServiceImpl<AdminMapper, Admin> implements AdminService {
}
