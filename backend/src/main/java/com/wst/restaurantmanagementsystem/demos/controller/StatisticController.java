package com.wst.restaurantmanagementsystem.demos.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wst.restaurantmanagementsystem.demos.entity.Statistic;
import com.wst.restaurantmanagementsystem.demos.service.StatisticService;
import com.wst.restaurantmanagementsystem.demos.utils.Code;
import com.wst.restaurantmanagementsystem.demos.utils.R;
import com.wst.restaurantmanagementsystem.demos.utils.TimeUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 *
 **/
@RestController
@RequestMapping("statistic")
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    /**
     * 时间格式化
     */
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/");

    /**
     * 图片保存路径
     */
    @Value("${file-save-path}")
    private String fileSavePath;


    @PostMapping("/upload")
    public R uploadPicture(@RequestParam("file") MultipartFile file, HttpServletRequest request) {

        String directory = simpleDateFormat.format(TimeUtil.getTime());



        File dir = new File(fileSavePath + directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        System.out.println("图片上传，保存的位置:" + fileSavePath + directory);


        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString().replaceAll("-", "") + suffix;


        File newFile = new File(fileSavePath + directory + newFileName);

        try {
            file.transferTo(newFile);

            String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/images/" + directory + newFileName;
            System.out.println("图片上传，访问URL：" + url);
            return new R(Code.WORK_OK, "上传成功", url);
        } catch (IOException e) {
            return new R(Code.WORK_ERR, "IO异常");
        }
    }

    @PostMapping("/list/{current}/{pageSize}")
    public R selectPage(@PathVariable("current") long current,@PathVariable("pageSize") long pageSize,
                        @RequestBody Statistic statistic){
        //mybatis-plus分页
        Page<Statistic> page = new Page<>(current, pageSize);
        QueryWrapper<Statistic> wrapper = new QueryWrapper<>();
        String sort = statistic.getSort();
        Integer createyear = statistic.getCreateyear();
        Integer createmonth = statistic.getCreatemonth();
        Integer createday = statistic.getCreateday();
        if(!StringUtils.isEmpty(sort))
            wrapper.like("sort",sort);
        if(createyear != null)
            wrapper.eq("createyear",createyear);
        if(createmonth != null)
            wrapper.eq("createmonth",createmonth);
        if(createday != null)
            wrapper.eq("createday",createday);
        wrapper.orderByDesc("gmt_create");

        Page<Statistic> result = statisticService.selectPage(page, wrapper);
        if (StringUtils.isEmpty(String.valueOf(result.getRecords()))){
            return new R(Code.WORK_ERR,"查询为空");
        }
        return new R(Code.WORK_OK,"操作成功",result);
    }

    @PostMapping("/add-one-statistic")
    public R addStatisticInfo(@RequestBody Statistic statistic){
        int flag = statisticService.addStatisticInfo(statistic);
        if (flag != 1){
            return new R(Code.WORK_ERR,"新增产品失败！");
        }else {
            return new R(Code.WORK_OK,"新增产品成功！");
        }
    }


    @GetMapping("/get-one-statistic/{id}")
    public R getOneStatistic(@PathVariable("id") Integer id){
        Statistic result = statisticService.getOneStatistic(id);
        return new R(Code.WORK_OK,"查询成功",result);
    }

}