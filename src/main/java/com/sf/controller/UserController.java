package com.sf.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.bean.UserBean;
import com.sf.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("addUser/{sex}/{name}")
    public boolean addUser(@PathVariable("name") String name,
                           @PathVariable("sex") String sex){
        return userService.addUser(new UserBean().setName(name).setSex(sex));
    }

    @GetMapping("delUser/{id}")
    public int delUser(@PathVariable("id") Long id) {
        int num = userService.delUserById(id);
        log.info("delUser -> {}", id);
        return num;
    }

    @GetMapping("getUsers")
    public Page<UserBean> getUsers() {
        Page<UserBean> userPage = userService.queryUsers(1L, 3L);
        log.info("userList -> {}", JSON.toJSONString(userPage.getRecords()));
        return userPage;
    }
}
