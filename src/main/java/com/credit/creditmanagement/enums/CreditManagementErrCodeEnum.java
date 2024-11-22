package com.credit.creditmanagement.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CreditManagementErrCodeEnum {

    EMPTY_PARAM("10000", "请求参数为空"),
    INITIAL_CREDIT_ACCOUNT_ERROR("10001", "初始化账户异常"),
    AMOUNT_TYPE_NOT_EXIST("10002", "账户类型不存在"),
    ALREADY_APPLY_ACCOUNT_AMOUNT("10003", "已经申请过信用账户额度"),
    INSERT_CREDIT_ACCOUNT_AMOUNT_ERROR("10004", "新增用户初始化额度失败"),
    INSERT_CREDIT_ACCOUNT_AMOUNT_TRANSACTION_LOG_ERROR("10005", "新增用户初始化额度流水失败"),
    OBTAIN_SEQ_NO_ERROR("10006", "获取序列号失败"),
    ;

    private final String code;
    private final String message;

}
