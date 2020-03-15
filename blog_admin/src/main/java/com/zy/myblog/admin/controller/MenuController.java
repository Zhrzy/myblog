package com.zy.myblog.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zy.myblog.admin.constant.SysConstant;
import com.zy.myblog.admin.service.CategoryMenuService;
import com.zy.myblog.xx.entity.CategoryMenu;
import com.zy.myblog.xx.utils.ResultUtil;
import com.zy.myblog.xx.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author zy 1716457206@qq.com
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private CategoryMenuService categoryMenuService;

    @RequestMapping("/getAllMenu")
    public String getAllMenu(){
        QueryWrapper<CategoryMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("menu_level", "1");
        queryWrapper.orderByDesc("sort");
        List<CategoryMenu> parent = categoryMenuService.list(queryWrapper);//获取纯父菜单

        //获取所有的ID，去寻找他的子目录
        List<String> parentUidList = new ArrayList<String>(); //父菜单id
        parent.forEach(item -> {
            if (!StringUtils.isEmpty(item.getUid())) {
                parentUidList.add(item.getUid());
            }
        });

        QueryWrapper<CategoryMenu> childWrapper = new QueryWrapper<>();
        childWrapper.in("parent_uid", parentUidList);
        Collection<CategoryMenu> allChild = categoryMenuService.list(childWrapper); //子菜单
        for (CategoryMenu parentItem : parent) {//遍历每个纯父菜单
            List<CategoryMenu> childList = new ArrayList<>();//接收子菜单
            for (CategoryMenu item : allChild) {
                if (item.getParentUid().equals(parentItem.getUid())) {
                    childList.add(item);
                }
            }
            //排序
            Collections.sort(childList, new Comparator<CategoryMenu>() {
                /*
                 * int compare(CategoryMenu p1, CategoryMenu p2) 返回一个基本类型的整型，
                 * 返回负数表示：p1 小于p2，
                 * 返回0 表示：p1和p2相等，
                 * 返回正数表示：p1大于p2
                 */
                @Override
                public int compare(CategoryMenu o1, CategoryMenu o2) {
                    //按照CategoryMenu的Sort进行降序排列
                    if (o1.getSort() > o2.getSort()) {
                        return -1;
                    }
                    if (o1.getSort().equals(o2.getSort())) {
                        return 0;
                    }
                    return 1;
                }
            });
            parentItem.setChildCategoryMenu(childList);
        }
        return ResultUtil.result(SysConstant.SUCCESS, "成功",parent);
    }

}
