package com.credit.creditmanagement.mapper;

import com.credit.creditmanagement.entity.CreditAccount;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface CreditAccountMapper {

    CreditAccount selectCreditAccountByIdCardNo(@Param("idCardNo") String idCardNo);

    CreditAccount selectCreditAccountByIdCardNoForUpdate(@Param("idCardNo") String idCardNo);

    int insertCreditAccount(@Param("creditAccount") CreditAccount creditAccount);

    int updateCreditAccountAmountById(@Param("id") Long id, @Param("amount")BigDecimal amount);


}
