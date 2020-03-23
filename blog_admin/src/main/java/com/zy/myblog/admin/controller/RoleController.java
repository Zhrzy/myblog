package com.zy.myblog.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.myblog.admin.service.CategoryMenuService;
import com.zy.myblog.admin.service.RoleService;
import com.zy.myblog.xx.entity.Admin;
import com.zy.myblog.xx.entity.CategoryMenu;
import com.zy.myblog.xx.entity.Role;
import com.zy.myblog.xx.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zy 1716457206@qq.com
 */
@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;
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
        List<Role> list  = page.getRecords();
        list.forEach(role -> {


        });
        pageList.setRecords(list);

        return ResultUtil.result("success", "分页查询成功", pageList);
    }
}
