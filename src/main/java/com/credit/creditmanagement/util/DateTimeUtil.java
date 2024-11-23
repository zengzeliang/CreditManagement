package com.credit.creditmanagement.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static String getCurTime(){
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmssSSS-");
        // 将当前时间按照指定格式转换成字符串
        String formattedDateTime = now.format(formatter);
        return formattedDateTime;
    }
}
