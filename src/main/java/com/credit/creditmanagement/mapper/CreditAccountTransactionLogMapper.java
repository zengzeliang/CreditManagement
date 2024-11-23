package com.credit.creditmanagement.mapper;

import com.credit.creditmanagement.entity.AmountCategory;
import com.credit.creditmanagement.entity.CreditAccount;
import com.credit.creditmanagement.entity.CreditAccountTransactionLog;
import org.apache.ibatis.annotations.Param;

public interface CreditAccountTransactionLogMapper {
    int insertCreditAccountTransactionLog(@Param("creditAccountTransactionLog") CreditAccountTransactionLog creditAccountTransactionLog);

    int updateCreditAccountTransactionLog(@Param("creditAccountTransactionLog") CreditAccountTransactionLog creditAccountTransactionLog);
}
