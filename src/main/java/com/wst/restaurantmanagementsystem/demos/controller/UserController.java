package com.wst.restaurantmanagementsystem.demos.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wst.restaurantmanagementsystem.demos.entity.User;
import com.wst.restaurantmanagementsystem.demos.service.UserService;
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
import java.util.Date;
import java.util.UUID;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 *
 **/
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

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
                        @RequestBody User user){
        //mybatis-plus分页
        Page<User> page = new Page<>(current, pageSize);
        QueryWrapper<User> wrapper = new QueryWrapper<>();

        String name = user.getName();
        if (!StringUtils.isEmpty(name)){
            wrapper.like("name",name);
        }
        //wrapper.eq("is_deleted","0");
        wrapper.orderByDesc("gmt_modified");

        Page<User> result = userService.selectPage(page, wrapper);
        if (StringUtils.isEmpty(String.valueOf(result.getRecords()))){
            return new R(Code.WORK_ERR,"查询为空");
        }
        return new R(Code.WORK_OK,"操作成功",result);
    }

    @PostMapping("/add-one-user")
    public R addUserInfo(@RequestBody User user){
        int flag = userService.addUserInfo(user);
        if (flag != 1){
            return new R(Code.WORK_ERR,"新增用户失败！");
        }else {
            return new R(Code.WORK_OK,"新增用户成功！");
        }
    }


    @GetMapping("/get-one-user/{id}")
    public R getOneUser(@PathVariable("id") Integer id){
        User result = userService.getOneUser(id);
        if (!Strings.isNotEmpty(result.getName())){
            return new R(Code.WORK_ERR,"查询失败！");
        }
        return new R(Code.WORK_OK,"查询成功",result);
    }




    @PostMapping("/upd-one-user")
    public R updOneUser(@RequestBody User user){
        int flag = userService.updOneUser(user);
        if (flag != 1){
            return new R(Code.WORK_ERR,"修改用户信息失败！");
        }else {
            return new R(Code.WORK_OK,"修改用户信息成功！");
        }
    }


    @PostMapping("/becomevip-one-user")
    public R deleteOneUser(@RequestBody User user){
        int flag = userService.becomeVip(user);
        if (flag != 1){
            return new R(Code.WORK_ERR,"成为vip失败！");
        }else {
            return new R(Code.WORK_OK,"成为vip成功！");
        }
    }

}
