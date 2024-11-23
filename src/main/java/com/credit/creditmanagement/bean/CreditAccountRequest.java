package com.credit.creditmanagement.bean;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditAccountRequest {
    private String idCardNo;
    private BigDecimal amount;
}
