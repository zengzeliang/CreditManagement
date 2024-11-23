package com.credit.creditmanagement.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class CreditManagementException extends RuntimeException{
    private String errorCode;

    private String errMsg;

    public CreditManagementException(String errorCode, String errMsg){
        super(errMsg);
        this.errorCode = errorCode;
        this.errMsg = errMsg;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
