package com.credit.creditmanagement.enums;

public enum TransLogStatusEnum {
    /**
     * 开始交易
     */
    START_TRANS_LOG("start"),
    /**
     * 交易成功
     */
    SUCCESS("success"),
    /**
     * 交易失败
     */
    FAIL("fail"),
    ;

    TransLogStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;
}
