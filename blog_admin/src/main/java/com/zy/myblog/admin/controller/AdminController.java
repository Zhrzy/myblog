package com.zy.myblog.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.myblog.admin.constant.SysConstant;
import com.zy.myblog.admin.constant.SysDefaultConf;
import com.zy.myblog.admin.jwt.JwtTokenUtil;
import com.zy.myblog.admin.service.AdminService;
import com.zy.myblog.admin.service.RoleService;
import com.zy.myblog.xx.entity.Admin;
import com.zy.myblog.xx.entity.Role;
import com.zy.myblog.xx.utils.ResultUtil;
import com.zy.myblog.xx.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    /** @Author zy
     * @Description 获取管理员列表
     * @Param
     * @return、
     **/
    @RequestMapping("/getAdminList")
    public String getAdminList(@RequestParam(name = "keyword",required = false) String keyword,
                               @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                               @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize){

        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();//条件构造器
        queryWrapper.select(Admin.class,i->!i.getColumn().equals("pass_word"));//不查询密码
        if(keyword !=null){
            queryWrapper.like("user_name",keyword).or().like("nick_name",keyword);//模糊查询条件
        }
        Page<Admin> page = new Page<>();//分页条件
        page.setCurrent(currentPage);//页数
        page.setSize(pageSize);//每页条数
        IPage<Admin> pageList = adminService.page(page, queryWrapper);//根据分页参数和条件构造器查询
        List<Admin> list = pageList.getRecords(); //获取用户信息
        //获取角色信息
        list.forEach(item->{
            Role role = roleService.getById(item.getRoleUid());
            item.setRole(role);
        });
        return ResultUtil.result("success", "分页查询成功", pageList);
    }

    @PostMapping(value = "/addAdmin")
    public String addAdmin(HttpServletRequest request, @RequestBody Admin admin) {
        String userName = admin.getUserName();
        String avatar = admin.getAvatar();
        QueryWrapper<Admin> userNameWrapper = new QueryWrapper<>();
        QueryWrapper<Admin> emailWrapper = new QueryWrapper<>();
        QueryWrapper<Admin> mobileWrapper = new QueryWrapper<>();
        userNameWrapper.eq(SysConstant.USERNAME, userName);
        Admin checkAdmin = adminService.getOne(userNameWrapper);
        //判断用户是否存在
        if (checkAdmin == null) {
            emailWrapper.eq(SysConstant.EMAIL,admin.getEmail());
            mobileWrapper.eq(SysConstant.MOBILE, admin.getMobile());
            Admin checkEmail = adminService.getOne(emailWrapper);
            Admin checkMobile = adminService.getOne(mobileWrapper);
            if (checkEmail!=null && checkMobile!=null){
                ResultUtil.result(SysConstant.ERROR, "邮箱和手机号已经存在，请重新输入！", "邮箱和手机号已经存在，请重新输入！");
            }
            if(checkEmail!=null){
                ResultUtil.result(SysConstant.ERROR, "邮箱已经存在，请重新输入！", "邮箱已经存在，请重新输入！");
            }
            if (checkMobile != null) {
                ResultUtil.result(SysConstant.ERROR, "手机号已经存在，请重新输入！", "手机号已经存在，请重新输入！");
            }
            if (StringUtils.isEmpty(avatar)){
                admin.setAvatar(SysDefaultConf.DEFAULT_AVATAR);
            }
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            admin.setStatus(SysDefaultConf.UNABLE);
            admin.setPassWord(encoder.encode(SysDefaultConf.DEFAULT_PWD));
            admin.setRoleUid("434994947c5a4ee3a710cd277357c7c3");
            adminService.save(admin);

           return  ResultUtil.result(SysConstant.SUCCESS,"操作成功！","操作成功！");
        } else {
            return ResultUtil.result(SysConstant.ERROR, "用户已经存在，请更换用户名！", "用户已经存在，请更换用户名！");
        }
    }


    @PostMapping("/editorAdmin")
    public  String editorAdmin(@RequestBody Admin updateBody){
        if (StringUtils.isEmpty(updateBody.getUid())) {
            return ResultUtil.result(SysConstant.ERROR, "必填项不能为空","必填项不能为空");
        }
        Admin admin = adminService.getById(updateBody.getUid());
        if (admin != null) {
            //判断修改的对象是否是超级管理员，超级管理员不能修改用户名
            if (admin.getUserName().equals("admin") && !updateBody.getUserName().equals("admin")) {
                return ResultUtil.result(SysConstant.ERROR, "超级管理员用户名必须为admin","超级管理员用户名必须为admin");
            }
            QueryWrapper<Admin> queryWrapper = new QueryWrapper<Admin>();
            queryWrapper.eq(SysConstant.USERNAME, updateBody.getUserName()).or().eq(SysConstant.EMAIL, updateBody.getEmail()).or().eq(SysConstant.MOBILE, updateBody.getMobile());
            List<Admin> adminList = adminService.list(queryWrapper);
            if (adminList != null) {
                for (Admin item : adminList) {
                    if (item.getUid().equals(updateBody.getUid())) {
                        continue;
                    } else {
                        return ResultUtil.result(SysConstant.ERROR, "修改失败：用户名存在，手机号已注册，邮箱已经注册","修改失败：用户名存在，手机号已注册，邮箱已经注册");
                    }
                }
            }
        }
        //updateBody.setPassWord(null);
        updateBody.updateById();
        return ResultUtil.result(SysConstant.SUCCESS, "更新管理员成功","更新管理员成功更新管理员成功");
    }

    @PostMapping("/deleteAdmin")
    public String deleteAdmin(@RequestParam(name = "adminUids", required = true) List<String> adminUids) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        if (adminUids.isEmpty()) {
            return ResultUtil.result(SysConstant.ERROR, "管理员uid不能为空","管理员uid不能为空");
        }
        QueryWrapper<Admin> adminqueryWrapper = new QueryWrapper<>();
        adminqueryWrapper.eq(SysConstant.USERNAME,"admin");
        String adminUid = adminService.getOne(adminqueryWrapper).getUid();
        for ( String item :adminUids){
            if(adminUid.equals(item)){
                return ResultUtil.result(SysConstant.ERROR, "禁止删除超级管理员","禁止删除超级管理员");
            }
        }
        queryWrapper.in(SysConstant.UID, adminUids);
        adminService.remove(queryWrapper);
        return ResultUtil.result(SysConstant.SUCCESS, "删除成功","删除成功");
    }


    @PostMapping("/changeStatus")
    public String changeStatus(@RequestParam(required = true,value = "uid") String uid ,@RequestParam(required = true,value = "status") int status) {
        System.out.println("dd");
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SysConstant.USERNAME, SysDefaultConf.ADMIN);
        Admin uidAdmin = adminService.getOne(queryWrapper);
        if(uid.equals(uidAdmin.getUid())){
            return ResultUtil.result(SysConstant.ERROR, "超级管理员不可禁用！", "超级管理员不可禁用！");
        }
        Admin admin = new Admin();
        admin.setUid(uid);
        if (status==1){
            admin.setStatus(2);
        }else {
            admin.setStatus(1);
        }
        admin.updateById();
        return ResultUtil.result(SysConstant.SUCCESS, "状态切换成功！", "状态切换成功!");
    }



}
