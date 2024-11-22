package com.credit.creditmanagement.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreditManagementException extends RuntimeException{
    private String errorCode;

    private String errMsg;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
