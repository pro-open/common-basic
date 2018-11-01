package com.project.basic.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.project.basic.conf.MessageSourceUtil;
import com.project.basic.vo.ResultInfo;

/**
 * 基础请求Controller
 * 
 * @Author  LiuBao
 * @Version 2.0
 * @Date 2018年10月26日
 */
public class AbsBaseController<T extends AbsBaseController<T>>{
    private static final Map<Class<?>, Logger> LOGGERS_MAP = new HashMap<Class<?>, Logger>();
    private static final Logger LOGGER = LoggerFactory.getLogger(AbsBaseController.class);
	private Class<T> controllerClass;
	
	@Autowired(required=false)
	private MessageSourceUtil messageSourceUtil;
    
    @SuppressWarnings("unchecked")
	public AbsBaseController() {
        Type[] types = ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments();
        this.controllerClass = (Class<T>) types[0];
    }

    public String getMessage(String code, Object[] args, String defaultMessage) {
        return messageSourceUtil.getMessage(code, args, defaultMessage);
    }

    public String getMessage(String code, Object... args) {
        return messageSourceUtil.getMessage(code, args);
    }
    
    protected Logger getLogger() {
        return getLogger(this.controllerClass);
    }

    protected Logger getLogger(Class<?> clazz) {
        Logger logger=LOGGERS_MAP.get(clazz);
        if (logger == null) {
            logger = LoggerFactory.getLogger(clazz);
            LOGGERS_MAP.put(clazz, logger);
        }
        return logger;
    }
    
    protected void clearLogger() {
        LOGGERS_MAP.clear();
    }
    
    /**
     * 封装ResultInfo对象
     */
    protected <V> ResultInfo<V> getSuccessResultInfo(V data) {
        return new ResultInfo<V>().buildSuccess(data);
    }
    
    /**
     * 失败信息调用返回结果
     */
    protected ResultInfo<?> getFailureResultInfo(String code, Object... args) {
        return new ResultInfo<>().buildFailure(code,messageSourceUtil.getMessage(code, args));
    }
    
    /**
     * 解析request对象请求参数信息
     */
    protected Map<String ,Object> getMapFromServletRequest(HttpServletRequest request) {
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String ,Object> resultMap = new HashMap<String ,Object>();
        if (requestMap != null) {
            for (String key : requestMap.keySet()) {
                String[] values = requestMap.get(key);
                resultMap.put(key, values.length == 1 ? values[0].trim() : values);
            }
        }
        LOGGER.debug("转换请求参数信息执行了,resultMap:{}",JSON.toJSONString(resultMap));
        return resultMap;
    }
    
    protected <V> V copyProperties(V dest, Object orig) {
        try {
            BeanUtils.copyProperties(dest,orig);
        } catch (IllegalAccessException e) {
            LOGGER.error("属性拷贝方法copyProperties异常:[orig={}]信息异常:{}",orig,e);
            dest=null;
        } catch (InvocationTargetException e) {
            LOGGER.error("属性拷贝方法copyProperties异常:[orig={}]信息异常:{}",orig,e);
            dest=null;
        }
        if(dest!=null){
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("属性拷贝方法转换非空实体结果为:{}",JSON.toJSONString(dest));
            }
        }
        return dest;
    }

}
