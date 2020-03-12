package com.zy.myblog.admin.mapper;

import com.zy.myblog.base.mapper.SuperMapper;
import com.zy.myblog.xx.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
public interface AdminMapper extends SuperMapper<Admin> {
}
