package com.credit.creditmanagement.bean;

import java.io.Serializable;

/**
 * 返回结果类
 * @param <T>
 */
public class ServiceResult<T> implements Serializable {
    T result;
    protected boolean success;
    private String errorCode;
    private String errorMsg;

    public static <T> ServiceResult<T> getSuccessResult(T v) {
        ServiceResult<T> result = new ServiceResult<>();
        result.setSuccess(true);
        result.setResult(v);
        return result;
    }

    public static <T> ServiceResult<T> getFailureResult(String errorCode, String errorMsg) {
        ServiceResult<T> result = new ServiceResult<>();
        result.setSuccess(false);
        result.setErrorCode(errorCode);
        result.setErrorMsg(errorMsg);
        return result;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
