package com.credit.creditmanagement.bean;

import lombok.Data;

@Data
public class InitializeCreditAccountRequest {
    private String username;
    private String idCardNo;
    private String amountType;
    private String address;
}
