package com.wst.restaurantmanagementsystem.demos.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wst.restaurantmanagementsystem.demos.entity.Statistic;

/**
 *
 **/
public interface StatisticService {


    Page<Statistic> selectPage(Page<Statistic> page, QueryWrapper<Statistic> wrapper);

    int addStatisticInfo(Statistic statistic);

    Statistic getOneStatistic(Integer id);


}