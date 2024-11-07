package com.wst.restaurantmanagementsystem.demos.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 **/
@Data
@TableName("user")
public class User implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 头像
     */
    @TableField("picture")
    private String picture;

    /**
     * 名称
     */
    @TableField("name")
    private String name;


    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 介绍
     */
    @TableField("introduce")
    private String introduce;



    /**
     * 是否vip
     */
    @TableField("is_vip")
    private Integer isVip;

    @TableField("is_manager")
    private Integer isManager;

    /**
     * 创建时间
     */
    @TableField("gmt_create")
    private Date gmtCreate;

    /**
     * 修改时间
     */
    @TableField("gmt_modified")
    private Date gmtModified;
}
