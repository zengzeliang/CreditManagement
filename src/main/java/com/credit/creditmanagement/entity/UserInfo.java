package com.credit.creditmanagement.entity;

import lombok.Data;

/**
 * 用户基本信息表
 */
@Data
public class UserInfo {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 身份证号
     */
    private String idCardNo;
    /**
     * 用户住址
     */
    private String address;
}
