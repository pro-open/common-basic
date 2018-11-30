package com.project.basic.exception;

/**
 * 未授权操作异常
 * 
 * @Author  LiuBao
 * @Version 2.0
 * @Date 2018年11月30日
 */
public class NoAuthException extends BaseException {
    private static final long serialVersionUID = 1364225358754654702L;


    public NoAuthException(String erroeCode, String message) {
        super(erroeCode, message);
    }

    public NoAuthException(String erroeCode) {
        super(erroeCode);
    }

    public NoAuthException(String erroeCode, Throwable throwable) {
        super(null, throwable);
        setErroeCode(erroeCode);
    }

}