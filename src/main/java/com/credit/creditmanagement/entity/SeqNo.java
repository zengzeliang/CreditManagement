package com.credit.creditmanagement.entity;

import lombok.Data;

/**
 * 序列号表
 */
@Data
public class SeqNo {

    /**
     * 使用日期 + 主键值做每日的序列号，每日清空表数据
     */
    private Long id;

    /**
     * 序列号表占位符
     */
    private String value;

}
