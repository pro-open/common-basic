package com.project.basic.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.project.basic.service.IRedisService;

/**
 * 基础请求Controller
 * 
 * @Author  LiuBao
 * @Version 2.0
 * @Date 2018年10月26日
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class BaseController<T extends BaseController<T>> extends AbsBaseController<T>{
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);
	
    @Autowired(required=false)
    protected IRedisService iRedisService;
    
    protected long getTtl(String key) {
        long data= iRedisService.ttl(key);
        LOGGER.info("获取的ttl信息为:{}",data);
        return data;
    }
    
    protected String getCacheByKey(String cacheKey) {
        String value= iRedisService.getString(cacheKey);
        LOGGER.info("获取的getCacheByKey[cacheKey={}]信息为:{}",cacheKey,value);
        return value;
    }
    
    protected void setCache(String cacheKey,long seconds,String cacheValye) {
        iRedisService.set(cacheKey, cacheValye, seconds);
        LOGGER.info("获取的setCache[cacheKey={}]信息,result为:{}",cacheKey,true);
    }
    
    protected void expireByKey(String cacheKey) {
        // redisCacheService.expireByKey(cacheKey, 1);
        iRedisService.remove(cacheKey);
    }
    
}
