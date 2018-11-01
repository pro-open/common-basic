package com.project.basic.utils;

import java.awt.Color;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

public class CommonUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);
	private static final String MONEY_PATTERN = "############0.00";
	
	/**
	 * 获取随机字符
	 * 
	 * @return
	 */
	public static String getRandomChar() {
		char captchars[] = { 'a', 'b', 'c', 'd', 'e', '2', '3', '4', '5', '6', '7', '8', 'g', 'f', 'y', 'n', 'm', 'p',
				'w', 'x', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N' };
		// 生成0-captchars.length数字
		int rand = (int) Math.round(Math.random() * (captchars.length - 1));
		return String.valueOf(captchars[rand]);
	}

	/**
	 * 获取随机图片
	 * 
	 * @param fc
	 * @param bc
	 * @return
	 */
	public static Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		// 得到随机颜色
		return new Color(r, g, b);
	}
	/**
	 * 手机号验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isMobile(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}
	 /*
    	校验过程： 
    1、从卡号最后一位数字开始，逆向将奇数位(1、3、5等等)相加。 
    2、从卡号最后一位数字开始，逆向将偶数位数字，先乘以2（如果乘积为两位数，将个位十位数字相加，即将其减去9），再求和。 
    3、将奇数位总和加上偶数位总和，结果应该可以被10整除。       
    */   
        /** 
        * 校验银行卡卡号 
        */  
       public static boolean checkBankCard(String bankCard) {  
                if(bankCard.length() < 15 || bankCard.length() > 19) {
                    return false;
                }
                char bit = getBankCardCheckCode(bankCard.substring(0, bankCard.length() - 1));  
                if(bit == 'N'){  
                    return false;  
                }  
                return bankCard.charAt(bankCard.length() - 1) == bit;  
       }  

       /** 
        * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位 
        * @param nonCheckCodeBankCard 
        * @return 
        */  
       public static char getBankCardCheckCode(String nonCheckCodeBankCard){  
           if(nonCheckCodeBankCard == null || nonCheckCodeBankCard.trim().length() == 0  
                   || !nonCheckCodeBankCard.matches("\\d+")) {  
               //如果传的不是数据返回N  
               return 'N';  
           }  
           char[] chs = nonCheckCodeBankCard.trim().toCharArray();  
           int luhmSum = 0;  
           for(int i = chs.length - 1, j = 0; i >= 0; i--, j++) {  
               int k = chs[i] - '0';  
               if(j % 2 == 0) {  
                   k *= 2;  
                   k = k / 10 + k % 10;  
               }  
               luhmSum += k;             
           }  
           return (luhmSum % 10 == 0) ? '0' : (char)((10 - luhmSum % 10) + '0');  
       }  
       
      /**
       * 
       * @Description: 校验姓名
       * @author shenchao
       * @Created 2017年4月18日下午5:37:41
       * @since 1.0
       * @param name
       * @return
       */
       public static boolean checkName(String name) {
    	boolean flag = false;
    	if (StringUtils.isBlank(name) || name.length() > 10) {
    		return flag;
    	}
   		String pattern = "^([\u4E00-\uFA29]|[\uE7C7-\uE7F3]|[a-zA-Z])*$";
   		Pattern r = Pattern.compile(pattern);
   		Matcher m = r.matcher(name);
   		if (m.matches()) {
   			return true;
   		}
   		return flag;
       }
       
       /**
        * 
        * @Description: 获取属性名数组
        * @author shenchao
        * @param <T>
        * @Created 2017年4月19日下午3:11:17
        * @since 1.0
        * @param o
        * @return
        */
       public static String[] getFiledName(Object o) {
   		Field[] fields = o.getClass().getDeclaredFields();
   		String[] fieldNames = new String[fields.length];
   		for (int i = 0; i < fields.length; i++) {
   			System.out.println(fields[i].getType());
   			fieldNames[i] = fields[i].getName();
   		}
   		return fieldNames;
   	}

    /**
     * 获取IP
     * 
     * @param request
     * @return 获取真实的IP地址
     */
    public static String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("获取对应的x-forwarded-for的IP信息为:{}", ip);
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("获取对应的Proxy-Client-IP的IP信息为:{}", ip);
            }
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("获取对应的WL-Proxy-Client-IP的IP信息为:{}", ip);
            }
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("获取对应的X-Real-IP的IP信息为:{}", ip);
            }
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("获取对应的HTTP_CLIENT_IP的IP信息为:{}", ip);
            }
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("获取对应的HTTP_X_FORWARDED_FOR的IP信息为:{}", ip);
            }
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("获取对应的getRemoteAddr的IP信息为:{}", ip);
            }
        }
        if (ip != null) {
            int index = ip.indexOf(",");
            if (index != -1) {
                ip = ip.substring(0, index);
            }
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("最终获取的对应的IP信息为:{}", ip);
        }
        return ip;
    }
    
    /**
     * 解析request对象请求参数信息
     */
    public static  Map<String ,Object> getMapFromRequest(HttpServletRequest request) {
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String ,Object> resultMap = new HashMap<String ,Object>();
        if (requestMap != null) {
            for (String key : requestMap.keySet()) {
                String[] values = requestMap.get(key);
                resultMap.put(key, values.length == 1 ? values[0].trim() : values);
            }
        }
        return resultMap;
    }
    
   	/**
	 * 将金额转换为大写人民币
	 *
	 * @param money
	 * @return
	 */
	public static String getRMBCapitalMoney(double money) {
		if (money == 0)
			return "零圆整";

		String szChMoney = "", szNum = "";
		int iAddZero = 0;
		String mnUnit[] = { "分", "角", "圆" };
		String hzUnit[] = { "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿" };
		String hzNum[] = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
		szNum = CommonUtils.doubleToMoneyLA(money).replaceAll("[.]", "");
		if (szNum.charAt(0) == '-') {
			szChMoney = "负";
			szNum = szNum.substring(1);
		}
		int iLen = szNum.length();

		if (iLen > 18 || iLen == 0)
			return "";

		for (int i = 1; i <= iLen; i++) {
			int iNum = Integer.parseInt(szNum.substring(i - 1, i));
			if (iNum == 0) {
				if ((iLen - i - 2) % 4 == 0 && iLen - i - 3 > 0
						&& (iLen - i >= 8 || iAddZero < 3))
					szChMoney += hzUnit[(iLen - i - 3) % 8];
				iAddZero++;
			} else {
				if ((iAddZero != 0 && iLen - i >= 2) && (iLen - i - 1) % 4 != 0
						|| iAddZero >= 4)
					szChMoney += hzNum[0];
				szChMoney += hzNum[iNum];
				iAddZero = 0;
			}

			if (iAddZero < 1 || iLen - i == 2)
				if (iLen - i >= 3) {
					szChMoney += hzUnit[(iLen - i - 3) % 8];
				} else
					szChMoney += mnUnit[(iLen - i) % 3];
		}

		if ( "".equals(szChMoney.trim()))
			return "零圆整";

		if (szNum.endsWith("0"))
			szChMoney += "整";

		if (szChMoney.startsWith("圆"))
			szChMoney = szChMoney.substring(1);

		return szChMoney;
	}
	/**
	 * 以元为单位的金额转换为不带分节符的左对齐的字符串
	 *
	 * @param money
	 * @return
	 */
	public static String doubleToMoneyLA(double money) {
		DecimalFormat decimalFormat = new DecimalFormat(
				CommonUtils.MONEY_PATTERN);
		return decimalFormat.format(money);
	}

	/**
	 * 
	 * @Description: 去除数字前面的0
	 * @author shenchao
	 * @Created 2017年2月28日下午7:45:20
	 * @since 1.0
	 * @return
	 */
	public static String getMoveZero(String fess) {
		return fess.replaceAll("^(0+)", "");
	}
	
	/**
	 * 
	 * @Description: 去掉小数0
	 * @author shenchao
	 * @Created 2016年11月14日下午2:01:35
	 * @since 1.0
	 * @param fess
	 * @return
	 */
	public static String getPersist(String fess) {
		if (fess.indexOf(".") > 0) {
			// 正则表达
			fess = fess.replaceAll("0+?$", "");// 去掉后面无用的零
			fess = fess.replaceAll("[.]$", "");// 如小数点后面全是零则去掉小数点
		}
		return fess;
	}
	
	/** 
	 * 将map转换成url 
	 */  
	public static String getUrlParamsByMap(Map<String, Object> map) {  
	    if (map == null) {  
	        return "";  
	    }  
	    StringBuffer sb = new StringBuffer();  
	    for (Map.Entry<String, Object> entry : map.entrySet()) {  
	        sb.append(entry.getKey() + "=" + entry.getValue());  
	        sb.append("&");  
	    }  
	    String s = sb.toString();  
	    if (s.endsWith("&")) {  
	        s = StringUtils.substringBeforeLast(s, "&");  
	    }  
	    return s;  
	}  
	
	/** 
     * 获取request请求参数信息
     */  
	public static Map<String,Object> getQueryStringFromRequest(HttpServletRequest request) {
        return  WebUtils.getParametersStartingWith(request, null);
    }
	
	 /**
	  * 
	  * @Description: 金额验证
	  * @author shenchao
	  * @Created 2017年5月5日下午6:11:57
	  * @since 1.0
	  * @param str
	  * @return
	  */
    public static boolean isNumber(String str){
		if (StringUtils.isBlank(str)) {
			return false;
		}
		Pattern pattern = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$"); // 判断小数点后2位的数字的正则表达式
		Matcher match = pattern.matcher(str);
		return match.matches();

	}
}
