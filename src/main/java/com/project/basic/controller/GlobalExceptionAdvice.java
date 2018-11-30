package com.project.basic.controller;

import java.sql.SQLException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.xml.bind.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.project.basic.conf.MessageSourceUtil;
import com.project.basic.exception.AbsErrorCodeConstant;
import com.project.basic.exception.BaseServiceException;
import com.project.basic.exception.NoAuthException;
import com.project.basic.exception.RequestLimitException;
import com.project.basic.vo.ResultInfo;

/**
 * 全局异常处理器
 * 
 * @Author  LiuBao
 * @Version 2.0
 * @Date 2018年10月26日
 */
@ResponseBody
@ControllerAdvice
public class GlobalExceptionAdvice {
    private static Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionAdvice.class);

	@Autowired(required=false)
	private MessageSourceUtil messageSourceUtil;

	/**
	 * 400 - Bad Request
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public Object handleMissingServletRequestParameterException(HttpServletResponse response,MissingServletRequestParameterException exception) {
		LOGGER.error("缺少请求参数", exception);
		ResultInfo<String> failure = new ResultInfo<String>().buildFailure(AbsErrorCodeConstant.ERROR_CODE_99001,
                messageSourceUtil.getMessage(AbsErrorCodeConstant.ERROR_CODE_99001));
		return failure;
	}

	/**
	 * 400 - Bad Request
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public Object handleHttpMessageNotReadableException(HttpServletResponse response,HttpMessageNotReadableException exception) {
		LOGGER.error("参数解析失败", exception);
		ResultInfo<String> failure = new ResultInfo<String>().buildFailure(AbsErrorCodeConstant.ERROR_CODE_99002,
				messageSourceUtil.getMessage(AbsErrorCodeConstant.ERROR_CODE_99002));
        return failure;
	}

	/**
	 * 400 - Bad Request
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(BindException.class)
	public Object handleBindException(HttpServletResponse response,BindException exception) {
		LOGGER.error("参数绑定失败", exception);
		BindingResult result = exception.getBindingResult();
		FieldError error = result.getFieldError();
		String field = error.getField();
		String code = error.getDefaultMessage();
		String message = String.format("%s:%s", field, code);
		ResultInfo<String> failure = new ResultInfo<String>().buildFailure(AbsErrorCodeConstant.ERROR_CODE_99003, message);
        return failure;
	}

	/**
	 * 400 - Bad Request
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(ConstraintViolationException.class)
	public Object handleConstraintViolationException(HttpServletResponse response,ConstraintViolationException exception) {
		LOGGER.error("参数Constraint验证失败", exception);
		Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
		ConstraintViolation<?> violation = violations.iterator().next();
		String message = violation.getMessage();
		ResultInfo<String> failure = new ResultInfo<String>().buildFailure(AbsErrorCodeConstant.ERROR_CODE_99003, message);
        return failure;
	}

	/**
	 * 400 - Bad Request
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(ValidationException.class)
	public Object handleValidationException(HttpServletResponse response,ValidationException exception) {
		LOGGER.error("参数验证失败", exception);
		ResultInfo<String> failure = new ResultInfo<String>().buildFailure(AbsErrorCodeConstant.ERROR_CODE_99003,
				messageSourceUtil.getMessage(AbsErrorCodeConstant.ERROR_CODE_99003));
        return failure;
	}

	/**
	 * 405 - Method Not Allowed
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public Object handleHttpRequestMethodNotSupportedException(HttpServletRequest request,HttpServletResponse response,HttpRequestMethodNotSupportedException exception) {
	    String method = request.getMethod();
		LOGGER.error("不支持当前请求{}方法:{}", exception,method);
		ResultInfo<String> failure = new ResultInfo<String>().buildFailure(AbsErrorCodeConstant.ERROR_CODE_99005,
				messageSourceUtil.getMessage(AbsErrorCodeConstant.ERROR_CODE_99005,new Object[]{method}));
        return failure;
	}

	/**
	 * 415 - Unsupported Media Type
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public Object handleHttpMediaTypeNotSupportedException(HttpServletRequest request,HttpServletResponse response,HttpMediaTypeNotSupportedException exception) {
		String contentType = request.getContentType();
		LOGGER.error("不支持当前媒体类型[{}],error:{}", exception,contentType);
		ResultInfo<String> failure = new ResultInfo<String>().buildFailure(AbsErrorCodeConstant.ERROR_CODE_99000,
				messageSourceUtil.getMessage(AbsErrorCodeConstant.ERROR_CODE_99000,new Object[]{contentType}));
        return failure;
	}

	/**
	 * 500 - Internal Server Error
	 * NoHandlerFoundException
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(Exception.class)
	public Object handleException(HttpServletResponse response,Exception exception) {
		LOGGER.error("通用异常", exception);
		ResultInfo<String> failure = new ResultInfo<String>().buildFailure(AbsErrorCodeConstant.ERROR_CODE_DEFAULT,
				messageSourceUtil.getMessage(AbsErrorCodeConstant.ERROR_CODE_DEFAULT));
        return failure;
	}
	
	/**
	 * 404 NoHandlerFoundException
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(NoHandlerFoundException.class)
	public Object handleNoHandlerFoundException(HttpServletResponse response,NoHandlerFoundException exception) {
	    LOGGER.error("通用异常", exception);
	    ResultInfo<String> failure = new ResultInfo<String>().buildFailure(AbsErrorCodeConstant.ERROR_CODE_404,
	            messageSourceUtil.getMessage(AbsErrorCodeConstant.ERROR_CODE_404));
	    return failure;
	}

	/**
	 * 操作数据库出现异常:名称重复，外键关联
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(DataIntegrityViolationException.class)
	public Object handleDataIntegrityViolationException(HttpServletResponse response,DataIntegrityViolationException exception) {
		LOGGER.error("操作数据库更新/插入出现异常:", exception);
		ResultInfo<String> failure = new ResultInfo<String>().buildFailure(AbsErrorCodeConstant.ERROR_CODE_10003,
				messageSourceUtil.getMessage(AbsErrorCodeConstant.ERROR_CODE_10003));
        return failure;
	}
	/**
	 * 数据库异常,请稍候重试!
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(SQLException.class)
	public Object handleSQLException(HttpServletResponse response,SQLException exception) {
	    LOGGER.error("操作数据库出现异常:", exception);
	    ResultInfo<String> failure = new ResultInfo<String>().buildFailure(AbsErrorCodeConstant.ERROR_CODE_10002,
	            messageSourceUtil.getMessage(AbsErrorCodeConstant.ERROR_CODE_10002));
        return failure;
	}
	
	/**
     * 基础service服务相关异常
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BaseServiceException.class)
    public Object handleBaseServiceException(HttpServletResponse response,BaseServiceException exception) {
        LOGGER.error("基础service服务相关异常", exception);
        ResultInfo<String> failure = new ResultInfo<String>().buildFailure(exception.getErroeCode(),
                messageSourceUtil.getMessage(exception.getErroeCode()));
        return failure;
    }
    
    /**
     * Http请求次数超限相关异常
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(RequestLimitException.class)
    public Object handleRequestLimitException(HttpServletResponse response,RequestLimitException exception) {
        LOGGER.error("Http请求次数超限相关异常", exception);
        ResultInfo<String> failure = new ResultInfo<String>().buildFailure(exception.getErroeCode(),
                messageSourceUtil.getMessage(exception.getErroeCode()));
        return failure;
    }
    
    
	/**
	 * 400 - Bad Request
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Object handleMethodArgumentNotValidException(HttpServletResponse response,MethodArgumentNotValidException exception) {
		LOGGER.error("参数校验失败", exception);
		BindingResult result = exception.getBindingResult();
		FieldError error = result.getFieldError();
		String field = error.getField();
		String code = error.getDefaultMessage();
		Object rejectedValue = error.getRejectedValue();
		// 解析此处返回非法的字段名称，原始值，错误信息
		String message = String.format("%s:%s", field, code);
		ResultInfo<Object> failure = new ResultInfo<Object>(AbsErrorCodeConstant.ERROR_CODE_DEFAULT, message, rejectedValue);
        return failure;
	}
	
	 /**
     *NoAuthException 访问资源未授权
     */
    @ExceptionHandler(NoAuthException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleNoAuthException(HttpServletResponse response,NoAuthException exception) {
        LOGGER.error("访问资源未授权", exception);
        return new ResultInfo<String>().buildFailure(AbsErrorCodeConstant.ERROR_CODE_NOAUTH,  
                messageSourceUtil.getMessage(AbsErrorCodeConstant.ERROR_CODE_NOAUTH));
    }
	
	/**
	 *MultipartException文件信息有问题
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(MultipartException.class)
	public Object handleMultipartException(HttpServletResponse response,MultipartException exception) {
	    LOGGER.error("MultipartException文件信息有问题", exception);
	    ResultInfo<String> failure = new ResultInfo<String>().buildFailure(AbsErrorCodeConstant.ERROR_CODE_20004,  
	            messageSourceUtil.getMessage(AbsErrorCodeConstant.ERROR_CODE_20004,new Object[]{"10Mb"}));
        return failure;
	}
	
}