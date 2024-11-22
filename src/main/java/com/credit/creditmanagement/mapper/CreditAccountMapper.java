package com.credit.creditmanagement.mapper;

import com.credit.creditmanagement.entity.CreditAccount;
import org.apache.ibatis.annotations.Param;

public interface CreditAccountMapper {

    CreditAccount selectCreditAccountByIdCardNo(@Param("idCardNo") String idCardNo);

    int insertCreditAccount(@Param("creditAccount") CreditAccount creditAccount);

}
