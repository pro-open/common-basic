package com.project.basic.utils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	static String[] mobileAgents = { "iphone", "android", "ipad", "phone", "mobile", "wap", "netfront", "java",
	        "opera mobi", "opera mini", "ucweb", "windows ce", "symbian", "series", "webos", "sony", "blackberry",
	        "dopod", "nokia", "samsung", "palmsource", "xda", "pieplus", "meizu", "midp", "cldc", "motorola",
	        "foma", "docomo", "up.browser", "up.link", "blazer", "helio", "hosin", "huawei", "novarra", "coolpad",
	        "webos", "techfaith", "palmsource", "alcatel", "amoi", "ktouch", "nexian", "ericsson", "philips",
	        "sagem", "wellcom", "bunjalloo", "maui", "smartphone", "iemobile", "spice", "bird", "zte-", "longcos",
	        "pantech", "gionee", "portalmmm", "jig browser", "hiptop", "benq", "haier", "^lct", "320x320",
	        "240x320", "176x220", "w3c ", "acs-", "alav", "alca", "amoi", "audi", "avan", "benq", "bird", "blac",
	        "blaz", "brew", "cell", "cldc", "cmd-", "dang", "doco", "eric", "hipt", "inno", "ipaq", "java", "jigs",
	        "kddi", "keji", "leno", "lg-c", "lg-d", "lg-g", "lge-", "maui", "maxo", "midp", "mits", "mmef", "mobi",
	        "mot-", "moto", "mwbp", "nec-", "newt", "noki", "oper", "palm", "pana", "pant", "phil", "play", "port",
	        "prox", "qwap", "sage", "sams", "sany", "sch-", "sec-", "send", "seri", "sgh-", "shar", "sie-", "siem",
	        "smal", "smar", "sony", "sph-", "symb", "t-mo", "teli", "tim-", "tosh", "tsm-", "upg1", "upsi", "vk-v",
	        "voda", "wap-", "wapa", "wapi", "wapp", "wapr", "webc", "winw", "winw", "xda", "xda-",
	"Googlebot-Mobile" };
	
    public static boolean checkMoblie(String userAgent) {
        boolean isMoblie = false;
        if (StringUtils.isNoneBlank(userAgent)) {
            for (String mobileAgent : mobileAgents) {
                if (userAgent.toLowerCase().indexOf(mobileAgent) >= 0 && userAgent.toLowerCase().indexOf("windows nt") <= 0
                        && userAgent.toLowerCase().indexOf("macintosh") <= 0) {
                    isMoblie = true;
                    break;
                }
            }
        }
        return isMoblie&&checkMoblie2(userAgent);
    }
    
 // 字符串在编译时会被转码一次,所以是 "\\b" 
    // \B 是单词内部逻辑间隔(连着的两个字母字符之间的逻辑上的间隔) 
    static String phoneReg = "\\b(ip(hone|od)|android|opera m(ob|in)i"
      +"|windows (phone|ce)|blackberry"
      +"|s(ymbian|eries60|amsung)|p(laybook|alm|rofile/midp"
      +"|laystation portable)|nokia|fennec|htc[-_]"
      +"|mobile|up.browser|[1-4][0-9]{2}x[1-4][0-9]{2})\\b"; 
    static String tableReg = "\\b(ipad|tablet|(Nexus 7)|up.browser" +"|[1-4][0-9]{2}x[1-4][0-9]{2})\\b"; 
      
    // 移动设备正则匹配：手机端、平板
    static Pattern phonePat = Pattern.compile(phoneReg, Pattern.CASE_INSENSITIVE);
    static Pattern tablePat = Pattern.compile(tableReg, Pattern.CASE_INSENSITIVE);

    /**
     * 检测是否是移动设备访问
     * 
     * @Title: check
     * @Date :
     * @param userAgent
     *            浏览器标识
     * @return true:移动设备接入，false:pc端接入
     */
    public static boolean checkMoblie2(String userAgent) {
        if (null == userAgent) {
            userAgent = "";
        }
        // 匹配
        Matcher matcherPhone = phonePat.matcher(userAgent);
        Matcher matcherTable = tablePat.matcher(userAgent);
        if (matcherPhone.find() || matcherTable.find()) {
            return true;
        } else {
            return false;
        }
    }
	
}
