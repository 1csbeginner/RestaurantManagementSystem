package com.wst.restaurantmanagementsystem.demos.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wst.restaurantmanagementsystem.demos.entity.Statistic;
import com.wst.restaurantmanagementsystem.demos.mapper.StatisticMapper;
import com.wst.restaurantmanagementsystem.demos.service.StatisticService;
import com.wst.restaurantmanagementsystem.demos.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Calendar;
import java.util.Date;


/**
 *
 **/
@Service
public class StatisticServiceImpl implements StatisticService {

    @Autowired
    private StatisticMapper statisticMapper;


    @Override
    public Page<Statistic> selectPage(Page<Statistic> page, QueryWrapper<Statistic> wrapper) {
        return statisticMapper.selectPage(page, wrapper);
    }


    @Override
    public int addStatisticInfo(Statistic statistic) {
        Statistic entity = new Statistic();
        entity.setId(statistic.getId());
        entity.setPreview(statistic.getPreview());
        entity.setSort(statistic.getSort());
        entity.setEarning(statistic.getEarning());
        entity.setGmtCreate(TimeUtil.getTime());
        Calendar calendar = Calendar.getInstance();

        entity.setCreateyear(calendar.get(Calendar.YEAR));
        entity.setCreatemonth(calendar.get(Calendar.MONTH) + 1);
        entity.setCreateday(calendar.get(Calendar.DAY_OF_MONTH));
        return statisticMapper.insert(entity);
    }


    @Override
    public Statistic getOneStatistic(Integer id) {
        Statistic statistic = statisticMapper.selectById(id);
        return statistic;
    }

}