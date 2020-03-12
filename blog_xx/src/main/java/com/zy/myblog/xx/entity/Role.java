package com.zy.myblog.xx.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zy.myblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * <p>
 * 角色信息表
 * </p>
 */
@Data
@TableName("t_role")
public class Role extends SuperEntity<Role> {

    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 介绍
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String summary;

    /**
     * 该角色所能操作的菜单uid
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String categoryMenuUids;
}
