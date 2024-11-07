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
@TableName("product")
public class Product implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 产品图片
     */
    @TableField("picture")
    private String picture;
    /*
        分类
     */
    @TableField("sort")
    private String sort;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 介绍
     */
    @TableField("introduce")
    private String introduce;

    /**
     * 价格
     */
    @TableField("price")
    private Double price;

    /**
     * vip价格
     */
    @TableField("vip_price")
    private Double VipPrice;

    /**
     * 删除标志
     */
    @TableField("is_deleted")
    private Integer isDeleted;

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
