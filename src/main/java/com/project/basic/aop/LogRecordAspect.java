package com.project.basic.aop;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.validation.UnexpectedTypeException;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.mysql.jdbc.MysqlDataTruncation;
import com.project.basic.conf.MessageSourceUtil;
import com.project.basic.exception.AbsErrorCodeConstant;
import com.project.basic.exception.BaseServiceException;
import com.project.basic.exception.NoAuthException;
import com.project.basic.utils.CommonUtils;
import com.project.basic.vo.ResultInfo;


/**
 * 日志拦截类
 * 
 * @Author  LiuBao
 * @Version 2.0
 *   2017年9月4日
 */
@Component
@Aspect
@Order(10)
public class LogRecordAspect {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogRecordAspect.class);
	
    @Before("controllerAspect()")    
	public void before(JoinPoint joinPoint) {
	    LOGGER.debug("LogRecordAspect.@Before执行了...");
	}
	
    @AfterReturning(value = "controllerAspect()", argNames = "joinPoint,retValue", returning = "retValue")
    public void afterReturning(JoinPoint joinPoint, Object retValue) {
        LOGGER.debug("LogRecordAspect.@AfterReturning执行了...");
    }
 
    /**
     * 当BaseController相关类及子类处理发生异常,则会执行此位置代码,
     * 对应的@AfterReturning代码不会被执行
     */
    @AfterThrowing(value = "controllerAspect()", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Exception e) {
        LOGGER.debug("LogRecordAspect.@AfterThrowing执行了@@@@@@@@@@@@@@@@@@@@@@@@@");
    }
    
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.GetMapping)")
    public  void controllerAspect() {
    } 
    
    @Autowired
    private MessageSourceUtil messageSourceUtil;
    
    /**
     * 环绕通知
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Around("controllerAspect()")  
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        LOGGER.debug("LogRecordAspect.@Around环绕通知执行了...");
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();  
        String method = request.getMethod();
        String url = request.getRequestURI();
        String contentType = request.getContentType();
        contentType = contentType==null?" ":contentType.toLowerCase();
        if(StringUtils.isBlank(contentType)
                ||MediaType.MULTIPART_FORM_DATA_VALUE.contains(contentType)
                ||contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)){
            return joinPoint.proceed();
        }
        String jsonString="{}";
        if(!StringUtils.equalsIgnoreCase(RequestMethod.POST.name(), method)
                &&!StringUtils.equalsIgnoreCase(RequestMethod.GET.name(), method)
                &&!StringUtils.equalsIgnoreCase(RequestMethod.PUT.name(), method)
                &&!StringUtils.equalsIgnoreCase(RequestMethod.DELETE.name(), method)
                ){
        //if(!"post".equalsIgnoreCase(request.getMethod())){
            LOGGER.warn("LogRecordAspect.@Around环绕通知方法[{}]为非POST...",url);
        }else{
            List<Object> objs = new ArrayList<Object>();
            for (Object obj : joinPoint.getArgs()) {
                if (obj instanceof MultipartFile) {
                    MultipartFile commFile = (MultipartFile) obj;
                    Map map = new HashMap();
                    map.put("name", commFile.getName());
                    map.put("contentType", commFile.getContentType());
                    map.put("originalFilename", commFile.getOriginalFilename());
                    map.put("size", commFile.getSize());
                    objs.add(map);
                } else if(obj instanceof ServletResponse
                        ||obj instanceof ModelAndView
                        ||obj instanceof Model
                        ||obj instanceof PrintWriter
                        ){
                    continue;
                }else if(obj instanceof ServletRequest ){
                    try {
                        Map<String, Object> map = CommonUtils.getMapFromRequest((HttpServletRequest)obj);
                        if(MapUtils.isNotEmpty(map)){
                            objs.add(map);
                        }
                    } catch (Exception e) {
                        LOGGER.error("解析HttpServletRequest信息异常!",e);
                    }
                }else if(obj instanceof RequestParam){
                    if(obj!=null){
                        objs.add(obj);
                    }
                }else if(obj instanceof LinkedHashMap){
                    if(obj!=null){
                        objs.add(obj);
                    }
                }else{
                    /*//obj.getClass().isAnnotationPresent(RequestHeader.class)
                Signature signature = joinPoint.getSignature();
                MethodSignature methodSignature = (MethodSignature) signature;
                String[] parameterNames = methodSignature.getParameterNames();
                Method methods = methodSignature.getMethod();
                Annotation[][] parameterAnnotations = methods.getParameterAnnotations();
                for (Annotation[] annotations : parameterAnnotations) {
                    for (Annotation annotation : annotations) {
                        annotation.toString();
                    }
                }*/
                    objs.add(obj);
                }
            }
            try {
                jsonString = JSON.toJSONString(objs);
            } catch (Exception e) {
                LOGGER.error("JSON数据格式化异常!",e);
            }
            if(LOGGER.isInfoEnabled()){
                LOGGER.info("##########################################################");
                LOGGER.info("########@@Around request [{}][{}]url: {}" ,method,contentType, url);
                LOGGER.info("########@@Around request param: \n{}" , jsonString);
                LOGGER.info("##########################################################\n");
            }
        }
        
        Object retValue = null;  
        try {
            retValue = joinPoint.proceed();  
        } catch (JSONException e) {
            LOGGER.error("LogRecordAspect.请求JSON数据解析异常:",e);
            retValue=new ResultInfo<String>().buildFailure(AbsErrorCodeConstant.ERROR_CODE_99002, 
                    messageSourceUtil.getMessage(AbsErrorCodeConstant.ERROR_CODE_99002));
        } catch (BaseServiceException e) {
            LOGGER.error("LogRecordAspect.请求HttpServiceException异常:",e);
            retValue=new ResultInfo<String>().buildFailure(e.getErroeCode(), messageSourceUtil.getMessage(e.getErroeCode()));
        }  catch (NoAuthException e) {
            LOGGER.error("LogRecordAspect.请求NoAuthException异常:",e);
            retValue=new ResultInfo<String>().buildFailure(e.getErroeCode(), messageSourceUtil.getMessage(e.getErroeCode()));
        } catch (ConnectException e) {
            LOGGER.error("LogRecordAspect.请求异常:",e);
            retValue=new ResultInfo<String>().buildFailure(AbsErrorCodeConstant.ERROR_CODE_91001, 
                    messageSourceUtil.getMessage(AbsErrorCodeConstant.ERROR_CODE_91001));
        }catch (UnknownHostException e) {
            LOGGER.error("LogRecordAspect.请求异常:",e);
            retValue=new ResultInfo<String>().buildFailure(AbsErrorCodeConstant.ERROR_CODE_91003, 
                    messageSourceUtil.getMessage(AbsErrorCodeConstant.ERROR_CODE_91003));
        } catch (IOException e) {
            // 发生网络异常
            LOGGER.error("LogRecordAspect.返回值获取异常",e);         
            retValue=new ResultInfo<String>().buildFailure(AbsErrorCodeConstant.ERROR_CODE_91005, 
                    messageSourceUtil.getMessage(AbsErrorCodeConstant.ERROR_CODE_91005));
        } catch (DataIntegrityViolationException | MysqlDataTruncation | UnexpectedTypeException e) {
            LOGGER.error("LogRecordAspect.数据库存储字符串长度等相关异常",e);         
            retValue=new ResultInfo<String>().buildFailure(AbsErrorCodeConstant.ERROR_CODE_DEFAULT, 
                    messageSourceUtil.getMessage(AbsErrorCodeConstant.ERROR_CODE_DEFAULT));
        } catch (Exception e) {
            LOGGER.error("LogRecordAspect.返回值获取异常",e);         
            retValue=new ResultInfo<String>().buildFailure(AbsErrorCodeConstant.ERROR_CODE_DEFAULT, 
                    messageSourceUtil.getMessage(AbsErrorCodeConstant.ERROR_CODE_DEFAULT));
        }catch (Throwable e) {  
            LOGGER.warn("LogRecordAspect.返回值获取异常!",e);
            retValue=new ResultInfo<String>().buildFailure(AbsErrorCodeConstant.ERROR_CODE_DEFAULT, 
                    messageSourceUtil.getMessage(AbsErrorCodeConstant.ERROR_CODE_DEFAULT));
        }  
        
        jsonString = JSON.toJSONString(retValue);
        /*if (jsonString.length() > 1000) {
            jsonString = jsonString.substring(0, 1000);
        }*/
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            LOGGER.info("@@@@@@@@ @@Around (request[{}] [{}]url): {}" , method,contentType,url);
            LOGGER.info("@@@@@@@@ @@Around response:\n {}" , jsonString);
            LOGGER.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
        }
        return retValue;  
    }  
	
}
