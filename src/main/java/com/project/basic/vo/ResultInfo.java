package com.project.basic.vo;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.basic.dao.entity.ToString;
import com.project.basic.exception.AbsErrorCodeConstant;


/**
 * 统一返回数据实体类<非分页结果>
 * 
 * @Author  LiuBao
 * @Version 2.0
 * @Date 2018年10月26日
 */
public class ResultInfo<T>  extends ToString{
	private static final long serialVersionUID = 2858244548725271096L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ResultInfo.class);
	
	private String code;
	private String message=StringUtils.EMPTY;
	private T data;

	public ResultInfo() {
		this.code = AbsErrorCodeConstant.ZERO;
	}

	public ResultInfo(T data) {
		this();
		this.data = data;
	}

	public ResultInfo(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public ResultInfo(String code, String message, T data) {
		this(code, message);
		this.data = data;
	}

	public ResultInfo<T> buildSuccess() {
	    this.setCode(AbsErrorCodeConstant.ZERO);
		return this;
	}

	public ResultInfo<T> buildSuccess(T data) {
	    this.setCode(AbsErrorCodeConstant.ZERO);
		this.setData(data);
		return this;
	}

	public ResultInfo<T> buildFailure() {
		this.setCode(AbsErrorCodeConstant.FAILURE);
		return this;
	}

	public ResultInfo<T> buildFailure(String code, String message) {
		if(StringUtils.isNotBlank(code)){
			this.setCode(code);
		}else{
			this.setCode(AbsErrorCodeConstant.FAILURE);
		}
		if(LOGGER.isInfoEnabled()){
		    LOGGER.info("返回封装后的错误信息为:code={},message={}",this.getCode(),message);
		}
		this.setMessage(message);
		return this;
	}

	@JSONField(name="code")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

//	@JSONField(name="message")
	@JSONField(name="msg")
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@JSONField(name="data")
	@JsonProperty(value = "data")
	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
