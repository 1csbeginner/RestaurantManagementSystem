package com.wst.restaurantmanagementsystem.demos.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wst.restaurantmanagementsystem.demos.entity.User;

/**
 *
 **/
public interface UserService {


    Page<User> selectPage(Page<User> page, QueryWrapper<User> wrapper);

    int addUserInfo(User user);

    User getOneUser(Integer id);

    int becomeVip(User user);

    int updOneUser(User user);
}
