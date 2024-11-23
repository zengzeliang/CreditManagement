package com.credit.creditmanagement.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        // 设置时区为Asia/Shanghai
        dataSource.addDataSourceProperty("serverTimezone", "Asia/Shanghai");
        return dataSource;
    }
}