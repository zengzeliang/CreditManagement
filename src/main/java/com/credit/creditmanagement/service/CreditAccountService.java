package com.credit.creditmanagement.service;

import com.credit.creditmanagement.bean.CreditAccountRequest;
import com.credit.creditmanagement.bean.InitializeCreditAccountRequest;
import com.credit.creditmanagement.bean.ServiceResult;
import com.credit.creditmanagement.entity.CreditAccount;

public interface CreditAccountService {

    ServiceResult<Void> initialCreditAccount(InitializeCreditAccountRequest request);

    ServiceResult<Void> increaseCreditAccount(CreditAccountRequest request);

    ServiceResult<Void> decreaseCreditAccount(CreditAccountRequest request);

    ServiceResult<CreditAccount> queryCreditAccount(String idCardNo);
}
