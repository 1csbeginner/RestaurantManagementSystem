package com.wst.restaurantmanagementsystem.demos.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wst.restaurantmanagementsystem.demos.entity.Indent;
import com.wst.restaurantmanagementsystem.demos.service.IndentService;
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
@RequestMapping("indent")
public class IndentController {

    @Autowired
    private IndentService indentService;

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
                        @RequestBody Indent indent){
        //mybatis-plus分页
        Page<Indent> page = new Page<>(current, pageSize);
        QueryWrapper<Indent> wrapper = new QueryWrapper<>();
        Integer tablenumber = indent.getTablenumber();
        if (tablenumber != null) {
            wrapper.eq("tablenumber", tablenumber);}

        wrapper.orderByDesc("gmt_create");

        Page<Indent> result = indentService.selectPage(page, wrapper);
        if (StringUtils.isEmpty(String.valueOf(result.getRecords()))){
            return new R(Code.WORK_ERR,"查询为空");
        }
        return new R(Code.WORK_OK,"操作成功",result);
    }

    @PostMapping("/add-one-indent")
    public R addIndentInfo(@RequestBody Indent indent){
        int flag = indentService.addIndentInfo(indent);
        if (flag != 1){
            return new R(Code.WORK_ERR,"新增产品失败！");
        }else {
            return new R(Code.WORK_OK,"新增产品成功！");
        }
    }


    @GetMapping("/get-one-indent/{id}")
    public R getOneIndent(@PathVariable("id") Integer id){
        Indent result = indentService.getOneIndent(id);
        return new R(Code.WORK_OK,"查询成功",result);
    }

}
