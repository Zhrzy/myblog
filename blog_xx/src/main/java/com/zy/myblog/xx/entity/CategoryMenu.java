package com.zy.myblog.xx.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zy.myblog.base.entity.SuperEntity;
import lombok.Data;

import java.util.List;

/**
 * 路由菜单表

 **/
@Data
@TableName("t_category_menu")
public class CategoryMenu extends SuperEntity<CategoryMenu> implements Comparable<CategoryMenu> {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单级别 （一级分类，二级分类）
     */
    private Integer menuLevel;

    /**
     * 介绍
     */
    private String summary;

    /**
     * Icon图标
     */
    private String icon;

    /**
     * 父UID
     */
    private String parentUid;

    /**
     * URL地址
     */
    private String url;

    /**
     * 排序字段(越大越靠前)
     */
    private Integer sort;

    /**
     * 父菜单
     */
    @TableField(exist = false)
    private CategoryMenu parentCategoryMenu;

    /**
     * 子菜单
     */
    @TableField(exist = false)
    private List<CategoryMenu> childCategoryMenu;

    @Override
    public int compareTo(CategoryMenu o) {

        if (this.sort >= o.getSort()) {
            return -1;
        }
        return 1;
    }
}
