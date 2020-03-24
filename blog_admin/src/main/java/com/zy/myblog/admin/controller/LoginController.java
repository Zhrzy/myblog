package com.zy.myblog.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zy.myblog.admin.config.JwtAudienceConfig;
import com.zy.myblog.admin.constant.SysConstant;
import com.zy.myblog.admin.jwt.IpUtils;
import com.zy.myblog.admin.jwt.JwtTokenUtil;
import com.zy.myblog.admin.service.AdminService;
import com.zy.myblog.admin.service.CategoryMenuService;
import com.zy.myblog.admin.service.RoleService;
import com.zy.myblog.xx.entity.Admin;
import com.zy.myblog.xx.entity.CategoryMenu;
import com.zy.myblog.xx.entity.Role;
import com.zy.myblog.xx.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author zy 1716457206@qq.com
 */
@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CategoryMenuService categoryMenuService;

    @Autowired
    private JwtAudienceConfig jwtAudienceConfig;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value(value = "${tokenHead}")
    private String tokenHead;

    @Value(value = "${isRememberMeExpiresSecond}")
    private int longExpiresSecond;

    @PostMapping("/login")
    //@CrossOrigin(origins = "http://localhost:9528") 接口级别跨域处理
    public String login(HttpServletRequest request,
                        @RequestParam(name="username",required = true) String username,
                        @RequestParam(name = "password",required = true) String password,
                        @RequestParam(name="isRememberMe",required = false,defaultValue = "1") int isRememberMe){
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name",username);
        Admin admin = adminService.getOne(queryWrapper);
        if(admin==null){
            return ResultUtil.result("error","失败用户不存在","用户不存在");
        }
        //验证密码
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(password,admin.getPassWord())){
            return ResultUtil.result("error","失败密码错误","密码错误");
        }
        System.out.println("?");
        if (admin.getStatus()==2){
            return ResultUtil.result(SysConstant.ERROR, "用户被禁止登陆！", "用户被禁止登陆！");
        }

        QueryWrapper<Role> queryWrapper1  =new QueryWrapper<>();
        queryWrapper1.eq("uid",admin.getRoleUid());
        List<Role> roleList = roleService.list(queryWrapper1);
        if(roleList.size() <= 0) {
            return ResultUtil.result("error","失败","没有权限");
        }
        //权限名
        String roleNames="";
        for (Role role : roleList) {
            roleNames += (role.getRoleName()+",");
        }
        roleNames =roleNames.substring(0, roleNames.length() - 1);
        System.out.println("role="+roleNames);
        //过期时间
        long expiration = isRememberMe == 1 ? longExpiresSecond : jwtAudienceConfig.getExpiresSecond();
        //生成token
        String token = jwtTokenUtil.createJWT(
                admin.getUserName(),
                admin.getUid(),
                roleNames,
                jwtAudienceConfig.getClientId(),
                jwtAudienceConfig.getName(),
                expiration*1000
                
                );
        token = tokenHead+token;
        Map<Object,Object> map = new HashMap<>();
        map.put("token",token);

        //进行登录相关操作
        Integer count = admin.getLoginCount() + 1;
        admin.setLoginCount(count);
        admin.setLastLoginIp(IpUtils.getIpAddr(request));
        admin.setLastLoginTime(new Date());
        admin.updateById();
        return ResultUtil.result("success","登陆成功了",map);
    }

    @GetMapping(value = "/info")
    //@CrossOrigin(origins = "http://localhost:9528")
    public String info(HttpServletRequest request, @RequestParam(name = "token", required = false) String token) {
        Map<String, Object> map = new HashMap<>();
        System.out.println("/info:"+request.getAttribute("adminUid"));
        if (request.getAttribute("adminUid") ==  null) {
            return ResultUtil.result("error","失败（token过期）", "token用户过期");
        }
        Admin admin = adminService.getById(request.getAttribute("adminUid").toString());
        map.put("token", token);
        //获取图片
       /* if (StringUtils.isNotEmpty(admin.getAvatar())) {
            String pictureList = this.pictureFeignClient.getPicture(admin.getAvatar(), SysConf.FILE_SEGMENTATION);
            admin.setPhotoList(WebUtils.getPicture(pictureList));

            List<String> list = WebUtils.getPicture(pictureList);

            if (list.size() > 0) {
                map.put(SysConf.AVATAR, list.get(0));
            } else {
                map.put(SysConf.AVATAR, "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
            }
        }*/
       map.put("avatar",admin.getAvatar());
       map.put("name",admin.getNickName());
        //加载这些角色所能访问的菜单页面列表
        //1)获取该管理员所有角色
        List<String> roleUid = new ArrayList<>();
        roleUid.add(admin.getRoleUid());
        Collection<Role> roleList = roleService.listByIds(roleUid);
        map.put("roles", roleList);
        return ResultUtil.result("success","获取用户信息成功", map);
    }

    @RequestMapping(value = "/getRouter")
    //@CrossOrigin(origins = "http://localhost:9528")
    public String getRouter(HttpServletRequest request){
      Admin admin = adminService.getById(request.getAttribute("adminUid").toString());
      List<String> roleUidList  =  new ArrayList<>(); //用户角色uid
      roleUidList.add(admin.getRoleUid());
      Collection<Role> roleList = roleService.listByIds(roleUidList);//用户角色列表
      List<String> menuUidList = new ArrayList<>();//目录（路由）uid列表
        //遍历角色列表，将其菜单uid截取并保存为list集合
        roleList.forEach(item -> {
            String caetgoryMenuUids = item.getCategoryMenuUids();
            String[] uids = caetgoryMenuUids.replace("[", "").replace("]", "").replace("\"", "").split(",");
            //存入菜单uid列表
            for (int a = 0; a < uids.length; a++) {
                menuUidList.add(uids[a]);
            }
        });
     //获取菜单列表
     Collection<CategoryMenu>  menuList = categoryMenuService.listByIds(menuUidList);
     //菜单列表转换为list集合
     List<CategoryMenu> list = new ArrayList<>(menuList);
     Map<Object,Object> map = new HashMap<>();
     map.put("router",list);
        return ResultUtil.result("success", "获取路由成功", map);
    }

    @PostMapping(value = "/logout")
   // @CrossOrigin(origins = "http://localhost:9528")
    public String logout(@RequestParam(name = "token", required = false) String token) {
        String destroyToken = null;
        return ResultUtil.result("success", "退出成功","退出成功");
    }
}
