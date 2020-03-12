package com.zy.myblog.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.myblog.base.mapper.SuperMapper;
import com.zy.myblog.base.service.SuperService;
/*
*
* */
public class SuperServiceImpl<M extends SuperMapper<T>,T>
        extends ServiceImpl<M,T>
        implements SuperService<T> {


}
