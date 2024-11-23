-- 创建数据库
DROP DATABASE IF EXISTS credit_management;
CREATE DATABASE credit_management;

-- 创建额度类型表
DROP TABLE IF EXISTS `amount_category`;
CREATE TABLE `amount_category` (
                                   `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
                                   `amountType` VARCHAR(255),
                                   `amount` DECIMAL(19, 2),
                                   UNIQUE KEY `idx_amountType` (`amountType`)
);

-- 创建账户表
DROP TABLE IF EXISTS `credit_account`;
CREATE TABLE `credit_account` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                  `idCardNo` VARCHAR(255),
                                  `creditLimit` DECIMAL(19, 2),
                                  UNIQUE KEY `idx_idCardNo` (`idCardNo`)
);

-- 创建账户操作流水表
DROP TABLE IF EXISTS `credit_account_transaction_log`;
CREATE TABLE `credit_account_transaction_log` (
                                                  `seqNo` VARCHAR(255) NOT NULL PRIMARY KEY,
                                                  `creditAccountId` BIGINT,
                                                  `operation` VARCHAR(255),
                                                  `status` VARCHAR(255),
                                                  `operationTime` DATETIME,
                                                  `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 序列号生成表
DROP TABLE IF EXISTS `seq_no`;
CREATE TABLE `seq_no` (
                          `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          `value` CHAR(1)
);

-- 生成用户信息表
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
                             `userId` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                             `username` VARCHAR(255),
                             `idCardNo` VARCHAR(255),
                             `address` VARCHAR(255),
                             UNIQUE KEY `idx_idCardNo` (`idCardNo`)
);

-- 新增amount_category数据
insert into `amount_category` (`amountType`, `amount`) values ('1001', '5000.00');