package com.wst.restaurantmanagementsystem.demos.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wst.restaurantmanagementsystem.demos.entity.Product;
import com.wst.restaurantmanagementsystem.demos.mapper.ProductMapper;
import com.wst.restaurantmanagementsystem.demos.service.ProductService;
import com.wst.restaurantmanagementsystem.demos.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;

/**
 *
 **/
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;


    @Override
    public Page<Product> selectPage(Page<Product> page, QueryWrapper<Product> wrapper) {
        return productMapper.selectPage(page,wrapper);
    }


    @Override
    public int addProductInfo(Product product) {
        Product entity = new Product();
        entity.setId(product.getId());
        entity.setPicture(product.getPicture());
        entity.setName(product.getName());
        entity.setIntroduce(product.getIntroduce());
        entity.setSort(product.getSort());
        entity.setPrice(product.getPrice());
        entity.setVipPrice(product.getPrice());
        entity.setIsDeleted(0);
        entity.setGmtCreate(TimeUtil.getTime());
        entity.setGmtModified(TimeUtil.getTime());
        return productMapper.insert(entity);
    }


    @Override
    public Product getOneProduct(Integer id) {
        Product product = productMapper.selectById(id);
        return product;
    }


    @Override
    public int deleteOneProduct(Product product) {
        Product entity = new Product();
        entity.setId(product.getId());
        entity.setIsDeleted(1);
        entity.setGmtModified(TimeUtil.getTime());
        return productMapper.updateById(entity);
    }


    @Override
    public int updOneProduct(Product product) {
        Product entity = new Product();
        entity.setId(product.getId());
        entity.setPicture(product.getPicture());
        entity.setName(product.getName());
        entity.setSort(product.getSort());
        entity.setIntroduce(product.getIntroduce());
        entity.setPrice(product.getPrice());
        entity.setVipPrice(product.getVipPrice());
        entity.setGmtModified(TimeUtil.getTime());
        return productMapper.updateById(entity);
    }
}
