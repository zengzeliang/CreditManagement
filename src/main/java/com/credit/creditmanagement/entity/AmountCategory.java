package com.credit.creditmanagement.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

/**
 * 额度类型表
 */
@Data
public class AmountCategory {
    /**
     * 编号
     */
    @Id
    private Long id;
    /**
     * 额度类型
     */
    private String amountType;
    /**
     * 额度值
     */
    private BigDecimal amount;
}
