package com.credit.creditmanagement.controller;

import com.credit.creditmanagement.bean.InitializeCreditAccountRequest;
import com.credit.creditmanagement.bean.ServiceResult;
import com.credit.creditmanagement.service.CreditAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/credit")
@Slf4j
public class CreditAccountController {
    @Autowired
    private CreditAccountService creditAccountService;

    /**
     * 初始化额度
     * @param request
     * @return
     */
    @PostMapping("/initialize")
    public ServiceResult<Void> initialCreditAccount(@RequestBody InitializeCreditAccountRequest request) {
        return creditAccountService.initialCreditAccount(request);
    }

}
