package com.project.basic.utils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;

/**
 * JSON转换工具类
 * 
 * @Author LiuBao
 * @Version 2.0 2017年4月20日
 */
public class FastJsonUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FastJsonUtil.class);

    /**
     * json转换成javaBean数组
     */
    public static final <T> List<T> parseArray(String json, String path, Class<T> clazz) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("数据转换parseArray请求[path={}]参数为:{}",path,json);
        }
        if (path != null) {
            Object val = JSONPath.read(json, path);
            if (val != null) {
                return JSON.parseArray(val.toString(), clazz);
            }
        }
        return JSON.parseArray(json, clazz);
    }

    /**
     * javaBean转换成json字符串
     */
    public static final String toJSONString(Object object) {
        return JSON.toJSONString(object);
    }

    /**
     * json转换成javaBean
     */
    public static final <T> T parseObject(String json, String path, Class<T> clazz) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("数据转换parseObject请求[path={}]参数为:{}",path,json);
        }
        if (path != null) {
            Object val = JSONPath.read(json, path);
            if (val != null) {
                return JSON.parseObject(val.toString(), clazz);
            }
        }
        return JSON.parseObject(json, clazz);
    }
    
    public static final <T> T parseObject(String json, Class<T> clazz) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("数据转换parseObject请求参数为:{}",json);
        }
        return parseObject(json,null, clazz);
    }

}
