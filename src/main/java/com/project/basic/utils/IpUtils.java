package com.project.basic.utils;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @Author  LiuBao
 * @Version 2.0
 * @Date 2018年11月27日
 */
public class IpUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(IpUtils.class);
	private static List<String> list = null;
	public static String getIp(HttpServletRequest request) {  
		String ip ="";
        if (request.getHeader("x-forwarded-for") == null) {  
        	ip = request.getRemoteAddr();  
        }else{
        	ip = request.getHeader("x-forwarded-for");  
        }
		return ip;  
	}
	/**
	 * 判断是否AJAX跨域所需要的IP
	 * @param ips
	 * @param origin
	 * @return
	 */
	public static boolean isAjaxCorsIp(String ips, String origin) {
		LOGGER.debug("===========>>判断是否AJAX跨域所需要的IP,ips:{}",ips);
		if(StringUtils.isBlank(ips)){
			return false;
		}
		if("*".equalsIgnoreCase(ips.trim())){
			return true;
		}
		if(null==list){
			String[] split = ips.trim().split(",");
			list = Arrays.asList(split);
		}
		if(null==list){
			return false;
		}
		return list.contains(origin);  
	}
	
}
