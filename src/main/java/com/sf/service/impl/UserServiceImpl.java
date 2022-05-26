package com.sf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.bean.UserBean;
import com.sf.mapper.UserMapper;
import com.sf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserBean> implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public boolean addUser(UserBean user) {
        return this.save(user);
    }

    @Override
    public int delUserById(Long id) {
        return userMapper.delUserById(id);
    }

    @Override
    public Page<UserBean> queryUsers(Long page, Long limit) {
        //分页
        Page<UserBean> pageInfo = new Page<>(page, limit);
        //条件
        LambdaQueryWrapper<UserBean> query = new LambdaQueryWrapper<>();
        query.eq(UserBean::getSex, "男");
        return this.page(pageInfo, query);
    }
}
