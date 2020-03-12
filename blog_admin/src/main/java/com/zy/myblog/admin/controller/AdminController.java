package com.zy.myblog.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.myblog.admin.jwt.JwtTokenUtil;
import com.zy.myblog.admin.service.AdminService;
import com.zy.myblog.admin.service.RoleService;
import com.zy.myblog.base.entity.SuperEntity;
import com.zy.myblog.xx.entity.Admin;
import com.zy.myblog.xx.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private RoleService roleService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @RequestMapping("/getList")
    public String getList(){
        List<Admin> admin = adminService.list();
        Admin admin1 =adminService.getById("1f01cd1d2f474743b241d74008b12333");
        System.out.println(admin1);
        /*String token = jwtTokenUtil.createJWT(admin1.getUserName(),admin1.getUid(),"admin","098f6bcd4621d373cade4e832627b4f6",
                "mogublog",3600);*/
        return ResultUtil.result("success","成功",admin);
    }


    @RequestMapping("/getAdminList")
    public String getAdminList(@RequestParam(name = "keyword",required = false) String keyword,
                               @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                               @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize){

        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();//条件构造器
        queryWrapper.select(Admin.class,i->!i.getColumn().equals("pass_word"));//不查询密码
        queryWrapper.like("user_name",keyword).or().like("nick_name",keyword);//模糊查询条件
        Page<Admin> page = new Page<>();//分页条件
        page.setCurrent(currentPage);//页数
        page.setSize(pageSize);//每页条数
        IPage<Admin> pageList = adminService.page(page, queryWrapper);//根据分页参数和条件构造器查询
        List<Admin> list = pageList.getRecords();



        return ResultUtil.result("success", "分页查询成功", pageList);
    }


}
