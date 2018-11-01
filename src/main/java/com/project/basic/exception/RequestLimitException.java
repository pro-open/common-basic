package com.project.basic.exception;

/**
 * IP此时限制
 */
public class RequestLimitException extends BaseException {
    private static final long serialVersionUID = 1364225358754654702L;

//    public RequestLimitException() {
//        super("HTTP请求超出设定的限制次数!");
//    }

    public RequestLimitException(String erroeCode, String message) {
        super(erroeCode, message);
    }

    public RequestLimitException(String erroeCode) {
        super(erroeCode);
    }

    public RequestLimitException(String erroeCode, Throwable throwable) {
        super(null, throwable);
        setErroeCode(erroeCode);
    }

}