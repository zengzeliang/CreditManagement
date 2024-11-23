# CreditManagement

功能： 实现一个额度管理功能，模拟多用户进行不用类型额度申请、扣减的功能。具体包括初始化额度、新增额度、扣减额度、查询额度，用例和代码需要包含额度管理模块、模拟多用户操作的定时任务代码、测试用例。
一、额度管理模块
1.   支持多个额度账户管理
2.   支持额度初始化
     支持额度增加
     支持额度扣减
     支持额度查询
     额度为double类型即可
     二、定时任务功能
1.    定时进行对额度操作
2.   模拟多用户发起


文档地址：https://zzje8yozln.feishu.cn/wiki/EmFKwRG6niExgnkZRUjc96ORn8b


一、功能说明
包含用户初始化额度、增加额度、减少额度功能
二、创建mysql数据库和表信息
-- 创建数据库
DROP DATABASE IF EXISTS credit_management;
CREATE DATABASE credit_management;


-- 创建额度类型表
USE credit_management;
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

三、接口API
1. 查询额度
/api/credit/queryAmount?idCardNo=1234566

示例
/api/credit/queryAmount?idCardNo=1234567

3. 初始化额度
/api/credit/initialize

示例
{
    "username": "zzl",
    "idCardNo": "1234567",
    "amountType": "1001",
    "address": "浙江省杭州市"
}

5. 增加额度
/api/credit/increase

示例
{
    "idCardNo": "1234567",
    "amount": 3000.00
}

6. 减少额度
/api/credit/decrease

示例
{
    "idCardNo": "1234567",
    "amount": 1000.00
}

四、测试用例
<img width="709" alt="image" src="https://github.com/user-attachments/assets/049cc45c-315c-4ad2-ad93-e0c58f80ec62">


备注
定时任务执行时间修改
ScheduledTaskService 
@Scheduled(cron = "0 49 19 * * *") // 指定时:分 19:49
