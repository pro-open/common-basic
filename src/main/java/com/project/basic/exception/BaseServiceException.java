package com.project.basic.exception;

public class BaseServiceException extends BaseException {
    private static final long serialVersionUID = 3449623024482478847L;

    public BaseServiceException(String erroeCode, String message) {
        super(erroeCode, message);
    }

    public BaseServiceException(String erroeCode) {
        super(erroeCode);
    }

    public BaseServiceException(String erroeCode, Throwable throwable) {
        super(null, throwable);
        setErroeCode(erroeCode);
    }
}