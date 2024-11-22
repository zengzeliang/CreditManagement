package com.credit.creditmanagement.service;

import com.credit.creditmanagement.bean.InitializeCreditAccountRequest;
import com.credit.creditmanagement.bean.ServiceResult;

public interface CreditAccountService {

    ServiceResult<Void> initialCreditAccount(InitializeCreditAccountRequest request);

}
