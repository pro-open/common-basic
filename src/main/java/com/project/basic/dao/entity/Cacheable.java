package com.project.basic.dao.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 基础的缓存实体接口定义
 * 
 * @Author  LiuBao
 * @Version 2.0
 * @Date 2018年10月26日
 */
public interface Cacheable {
    
    //获取redis的key值
    @JSONField(serialize=true,deserialize=false)
    public abstract String getRedisKey();
    //是否缓存标识
    @JSONField(serialize=true,deserialize=false)
    public default boolean isCachIgnore(){
        return false;
    }

}
