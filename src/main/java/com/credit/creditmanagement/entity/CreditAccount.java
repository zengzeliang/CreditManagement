package com.credit.creditmanagement.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

/**
 * 账户类表
 */
@Data
public class CreditAccount {
    /**
     * 编号
     */
    @Id
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
