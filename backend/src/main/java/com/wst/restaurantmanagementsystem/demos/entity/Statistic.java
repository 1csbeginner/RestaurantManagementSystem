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
@TableName("statistic")
public class Statistic implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 总览
     */
    @TableField("preview")
    private String preview;

    @TableField("sort")
    private String sort;


    @TableField("createyear")
    private Integer createyear;

    @TableField("createmonth")
    private Integer createmonth;

    @TableField("createday")
    private Integer createday;

    /**
     * 收入
     */
    @TableField("earning")
    private Double earning;

    /**
     * 创建时间
     */
    @TableField("gmt_create")
    private Date gmtCreate;

}