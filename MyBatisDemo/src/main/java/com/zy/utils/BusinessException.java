package com.zy.utils;

public class BusinessException extends RuntimeException{

    private String exceptionCode;  // 异常代号标识

    private String message;  // 异常内容

    public BusinessException(String exceptionCode,String message){
        super(exceptionCode+":"+message);
        this.exceptionCode=exceptionCode;
        this.message=message;
    }


    public String getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(String exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
