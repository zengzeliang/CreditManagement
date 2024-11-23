-- 创建数据库
create database credit_management;

-- 创建额度类型表
CREATE TABLE `amount_category` (
                                   `id` BIGINT NOT NULL PRIMARY KEY,
                                   `amount_type` VARCHAR(255),
                                   `amount` DECIMAL(19, 2),
                                   UNIQUE KEY `idx_amount_type` (`amount_type`)
);

-- 创建账户表
CREATE TABLE `credit_account` (
                                  `id` BIGINT NOT NULL PRIMARY KEY,
                                  `idCardNo` VARCHAR(255),
                                  `creditLimit` DECIMAL(19, 2),
                                  UNIQUE KEY `idx_idCardNo` (`idCardNo`)
);

-- 创建账户操作流水表
CREATE TABLE `credit_account_transaction_log` (
                                                  `seqNo` VARCHAR(255) NOT NULL PRIMARY KEY,
                                                  `creditAccountId` BIGINT,
                                                  `operation` VARCHAR(255),
                                                  `status` VARCHAR(255),
                                                  `operationTime` DATETIME,
                                                  `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 序列号生成表
CREATE TABLE `seq_no` (
                          `id` BIGINT NOT NULL PRIMARY KEY,
                          `value` CHAR(1)
);

-- 生成用户信息表
CREATE TABLE `user_info` (
                             `userId` BIGINT NOT NULL PRIMARY KEY,
                             `username` VARCHAR(255),
                             `idCardNo` VARCHAR(255),
                             `address` VARCHAR(255),
                             UNIQUE KEY `idx_idCardNo` (`idCardNo`)
);