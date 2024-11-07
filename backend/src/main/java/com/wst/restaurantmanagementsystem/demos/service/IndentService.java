package com.wst.restaurantmanagementsystem.demos.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wst.restaurantmanagementsystem.demos.entity.Indent;

/**
 *
 **/
public interface IndentService {


    Page<Indent> selectPage(Page<Indent> page, QueryWrapper<Indent> wrapper);

    int addIndentInfo(Indent indent);

    Indent getOneIndent(Integer id);


}
