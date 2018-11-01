package com.project.basic.conf;

import java.util.Locale;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * 国际化提示文案信息封装类
 * 
 * @Author  LiuBao
 * @Version 2.0
 * @Date 2018年10月26日
 */
@Component
public class MessageSourceUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSourceUtil.class);

	@Resource
	private MessageSource messageSource;

	/**
	 * 获取code对应的message
	 */
	public String getMessage(String code) {
		return getMessage(code, null);
	}

	/**
	 *
	 * 获取code对应的message
	 *
	 * @param code  ：对应messages配置的key.
	 * @param args  : 参数.
	 */
	public String getMessage(String code, Object[] args) {
		return getMessage(code, args, "");
	}

	/**
	 * 这里使用比较方便的方法，不依赖HttpServletRequest.
	 *
	 * @param code ：对应messages配置的key.
	 * @param args : 参数.
	 * @param defaultMessage  : 没有设置key的时候的默认值.
	 */
	public String getMessage(String code, Object[] args, String defaultMessage) {
		Locale locale = LocaleContextHolder.getLocale();
		//locale=Locale.SIMPLIFIED_CHINESE;
		if(!Locale.SIMPLIFIED_CHINESE.equals(locale)){
		    locale=Locale.US;
		}
		LOGGER.info("now the locale is:{}",locale);
		String message = messageSource.getMessage(code, args, defaultMessage, locale);
		return message;
	}

}
