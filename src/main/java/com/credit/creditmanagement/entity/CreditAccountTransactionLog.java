package com.credit.creditmanagement.entity;

import lombok.Data;

import java.sql.Timestamp;
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
     * 流水状态
     * @see com.credit.creditmanagement.enums.TransLogStatusEnum
     */
    private String status;

    /**
     * 操作时间
     */
    private Date operationTime;

    /**
     * 当前时间戳
     */
    private Timestamp timestamp;

}