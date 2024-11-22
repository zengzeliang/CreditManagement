package com.credit.creditmanagement.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 账户类表
 */
@Data
public class CreditAccount {
    /**
     * 编号
     */
    private Long id;
    /**
     * 身份证号
     */
    private String idCardNo;
    /**
     * 最大额度
     */
    private BigDecimal creditLimit;

}
