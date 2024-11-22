package com.credit.creditmanagement.entity;

import lombok.Data;

import java.util.Date;

/**
 * 流水表
 */
@Data
public class CreditAccountTransactionLog {

    /**
     * 流水号
     */
    private String seqNo;

    /**
     * 用户id
     */
    private Long creditAccountId;

    /**
     * 操作类型
     * @see com.credit.creditmanagement.enums.AccountOperationTypeEnum
     */
    private String operation;

    /**
     * 操作时间
     */
    private Date operationTime;

}