package com.wst.restaurantmanagementsystem.demos.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wst.restaurantmanagementsystem.demos.entity.Product;

/**
 *
 **/
public interface ProductService {


    Page<Product> selectPage(Page<Product> page, QueryWrapper<Product> wrapper);

    int addProductInfo(Product product);

    Product getOneProduct(Integer id);

    int deleteOneProduct(Product product);

    int updOneProduct(Product product);
}
