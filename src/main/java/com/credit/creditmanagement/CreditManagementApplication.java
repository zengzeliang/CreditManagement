package com.credit.creditmanagement;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.credit.creditmanagement.mapper")
public class CreditManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreditManagementApplication.class, args);

    }

}
