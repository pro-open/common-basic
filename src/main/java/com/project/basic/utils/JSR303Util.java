package com.project.basic.utils;
import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSR303的校验帮助类
 * 
 * @Author  LiuBao
 * @Version 2.0
 *   2017年12月14日
 */
public class JSR303Util {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSR303Util.class);
    
    private static Validator validator;

    static {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * 校验对象
     * @param object        待校验对象
     * @param groups        待校验的组
     * @throws ShiroException  校验不通过，则报RRException异常
     */
    public static void validateEntity(Object object, Class<?>... groups) throws ValidationException {
        if (null == object) {
            return;
        }
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            StringBuilder msg = new StringBuilder();
            for(ConstraintViolation<Object> constraint:  constraintViolations){
                msg.append(constraint.getMessage()).append("<br>");
            }
            throw new ValidationException(msg.toString());
        }
    }

    /**
     * 如果返回null则表示没有错误
     */
    public static String check(Object obj) {
        if (null == obj) {
            return "入参对象不能为空!";
        }
        Set<ConstraintViolation<Object>> validResult = validator.validate(obj);
        if (null != validResult && validResult.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (Iterator<ConstraintViolation<Object>> iterator = validResult.iterator(); iterator.hasNext();) {
                ConstraintViolation<Object> constraintViolation = (ConstraintViolation<Object>) iterator.next();
                if(StringUtils.isNotBlank(constraintViolation.getMessage())) {
                    sb.append(constraintViolation.getMessage()).append("、");
                } else {
                    sb.append(constraintViolation.getPropertyPath().toString()).append("不合法、");
                }
            }
            if (sb.lastIndexOf("、") == sb.length() - 1) {
                sb.delete(sb.length() - 1, sb.length());
            }
            LOGGER.error("校验异常信息:{}",sb.toString());
            return sb.toString();
        }
        return null;
    }

}