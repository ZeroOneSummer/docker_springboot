package com.sf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.bean.UserBean;


public interface UserService extends IService<UserBean> {

    boolean addUser(UserBean user);

    int delUserById(Long id);

    Page<UserBean> queryUsers(Long page, Long limit);
}
