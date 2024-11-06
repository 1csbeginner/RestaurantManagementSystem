package com.wst.restaurantmanagementsystem.demos.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wst.restaurantmanagementsystem.demos.entity.Indent;
import com.wst.restaurantmanagementsystem.demos.mapper.IndentMapper;
import com.wst.restaurantmanagementsystem.demos.service.IndentService;
import com.wst.restaurantmanagementsystem.demos.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 **/
@Service
public class IndentServiceImpl implements IndentService {

    @Autowired
    private IndentMapper indentMapper;


    @Override
    public Page<Indent> selectPage(Page<Indent> page, QueryWrapper<Indent> wrapper) {
        return indentMapper.selectPage(page, wrapper);
    }


    @Override
    public int addIndentInfo(Indent indent) {
        Indent entity = new Indent();
        entity.setId(indent.getId());
        entity.setPreview(indent.getPreview());
        entity.setPrice(indent.getPrice());
        entity.setTablenumber(indent.getTablenumber());
        entity.setGmtCreate(TimeUtil.getTime());

        return indentMapper.insert(entity);
    }


    @Override
    public Indent getOneIndent(Integer id) {
        Indent indent = indentMapper.selectById(id);
        return indent;
    }

}
