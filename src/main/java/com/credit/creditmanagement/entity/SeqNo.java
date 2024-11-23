package com.credit.creditmanagement.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * 序列号表
 */
@Data
public class SeqNo {

    /**
     * 使用日期 + 主键值做每日的序列号，每日清空表数据
     */
    @Id
    private Long id;

    /**
     * 序列号表占位符
     */
    private String value;

}
