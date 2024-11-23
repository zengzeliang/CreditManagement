package com.credit.creditmanagement.controller;

import com.credit.creditmanagement.bean.CreditAccountRequest;
import com.credit.creditmanagement.bean.InitializeCreditAccountRequest;
import com.credit.creditmanagement.bean.ServiceResult;
import com.credit.creditmanagement.entity.CreditAccount;
import com.credit.creditmanagement.service.CreditAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 增加额度
     */
    @PostMapping("/increase")
    public ServiceResult<Void> increaseCreditAccount(@RequestBody CreditAccountRequest request) {
        return creditAccountService.increaseCreditAccount(request);
    }

    /**
     * 扣减额度
     */
    @PostMapping("/decrease")
    public ServiceResult<Void> decreaseCreditAccount(@RequestBody CreditAccountRequest request) {
        return creditAccountService.decreaseCreditAccount(request);
    }

    /**
     * 查询额度
     */
    @GetMapping("/queryAmount")
    public ServiceResult<CreditAccount> queryCreditAccount(@RequestParam("idCardNo") String idCardNo) {
        return creditAccountService.queryCreditAccount(idCardNo);
    }
}
