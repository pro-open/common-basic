package com.project.basic.utils;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 
 * @Author  LiuBao
 * @Version 2.0
 *   2017年10月31日
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
    
}

