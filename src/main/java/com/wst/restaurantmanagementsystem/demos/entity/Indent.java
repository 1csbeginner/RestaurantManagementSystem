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
@TableName("indent")
public class Indent implements Serializable{

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

    /**
     * 台号
     */
    @TableField("tablenumber")
    private Integer tablenumber;

    /**
     * 价格
     */
    @TableField("price")
    private Double price;



    /**
     * 创建时间/
     */
    @TableField("gmt_create")
    private Date gmtCreate;

}
