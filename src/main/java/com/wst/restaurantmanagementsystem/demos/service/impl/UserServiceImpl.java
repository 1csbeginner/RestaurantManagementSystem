package com.wst.restaurantmanagementsystem.demos.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wst.restaurantmanagementsystem.demos.entity.User;
import com.wst.restaurantmanagementsystem.demos.mapper.UserMapper;
import com.wst.restaurantmanagementsystem.demos.service.UserService;
import com.wst.restaurantmanagementsystem.demos.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;

/**
 *
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public Page<User> selectPage(Page<User> page, QueryWrapper<User> wrapper) {
        return userMapper.selectPage(page,wrapper);
    }


    @Override
    public int addUserInfo(User user) {
        User entity = new User();
        entity.setId(user.getId());
        entity.setPicture(user.getPicture());
        entity.setName(user.getName());
        entity.setPassword(user.getPassword());
        entity.setIntroduce(user.getIntroduce());
        entity.setIsVip(0);
        entity.setGmtCreate(TimeUtil.getTime());
        entity.setGmtModified(TimeUtil.getTime());
        return userMapper.insert(entity);
    }


    @Override
    public User getOneUser(Integer id) {
        User user = userMapper.selectById(id);
        return user;
    }


    @Override
    public int becomeVip(User user) {
        User entity = new User();
        entity.setId(user.getId());
        entity.setIsVip(1);
        entity.setGmtModified(TimeUtil.getTime());
        return userMapper.updateById(entity);
    }


    @Override
    public int updOneUser(User user) {
        User entity = new User();
        entity.setId(user.getId());
        entity.setPicture(user.getPicture());
        entity.setName(user.getName());
        entity.setPassword(user.getPassword());
        entity.setIntroduce(user.getIntroduce());
        entity.setGmtModified(TimeUtil.getTime());
        return userMapper.updateById(entity);
    }
}
