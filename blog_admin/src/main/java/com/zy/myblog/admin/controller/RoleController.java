package com.zy.myblog.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.myblog.admin.constant.SysConstant;
import com.zy.myblog.admin.service.CategoryMenuService;
import com.zy.myblog.admin.service.RoleService;
import com.zy.myblog.xx.entity.Admin;
import com.zy.myblog.xx.entity.CategoryMenu;
import com.zy.myblog.xx.entity.Role;
import com.zy.myblog.xx.utils.ListUtils;
import com.zy.myblog.xx.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zy 1716457206@qq.com
 */
@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private CategoryMenuService menuService;


    @RequestMapping("/getRoleList")
    public String getRoleList(@RequestParam(name = "keyword",required = false) String keyword,
                               @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                               @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize){
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();//条件构造器
        if(keyword !=null){
            queryWrapper.like("role_name",keyword).or().like("summary",keyword);//模糊查询条件
        }
        Page<Role> page = new Page<>();//分页条件
        page.setCurrent(currentPage);//页数
        page.setSize(pageSize);//每页条数
        IPage<Role> pageList = roleService.page(page, queryWrapper);//根据分页参数和条件构造器查询
        QueryWrapper<CategoryMenu> parentMenuQueryWrapper = new QueryWrapper<>();
        parentMenuQueryWrapper.eq("menu_level","1");
        List<CategoryMenu> parentMenuList = menuService.list(parentMenuQueryWrapper);//父目录
        List<String> parentList = new ArrayList<>();
        for (int j=0;j<parentMenuList.size();j++){
            parentList.add(parentMenuList.get(j).getUid());
        }
        List<Role> list  = page.getRecords();
        list.forEach(role -> {
            String menuUidsString = role.getCategoryMenuUids();
            String menuUidsStringNo = menuUidsString.substring(1,menuUidsString.length()-1);
            List<String> menuUidList = Stream.of(menuUidsStringNo.split(",")).collect(Collectors.toList());
            for (int j=0;j<menuUidList.size();j++){
                menuUidList.set(j,menuUidList.get(j).substring(1,menuUidList.get(j).length()-1));
            }
            //过滤路由，获取交集算法，（只取子菜单）
            List<String> onlyChildUids=  ListUtils.receiveDefectList(menuUidList, parentList);
            for (int i =0 ;i<onlyChildUids.size();i++){
                onlyChildUids.set(i,"\""+onlyChildUids.get(i)+"\"");
            }
            role.setCategoryMenuUids(onlyChildUids.toString());
        });
        pageList.setRecords(list);
        return ResultUtil.result(SysConstant.SUCCESS, "分页查询成功", pageList);
    }
}
