package com.credit.creditmanagement.service;

import com.credit.creditmanagement.bean.CreditAccountRequest;
import com.credit.creditmanagement.bean.InitializeCreditAccountRequest;
import com.credit.creditmanagement.bean.ServiceResult;
import com.credit.creditmanagement.entity.CreditAccount;
import com.credit.creditmanagement.enums.CreditManagementErrCodeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CreditAccountServiceTest {

    @Autowired
    private CreditAccountService creditAccountService;

    /**
     * 测试账户类型不存在
     */
    @Test
    void initialCreditAccountForNotExistAmountType() {
        InitializeCreditAccountRequest initializeCreditAccountRequest = new InitializeCreditAccountRequest();
        initializeCreditAccountRequest.setIdCardNo("1234567");
        initializeCreditAccountRequest.setUsername("zzl");
        initializeCreditAccountRequest.setAmountType("-1001");
        initializeCreditAccountRequest.setAddress("浙江省杭州市");
        ServiceResult<Void> serviceResult = creditAccountService.initialCreditAccount(initializeCreditAccountRequest);
        assert serviceResult.getErrorCode().equals(CreditManagementErrCodeEnum.AMOUNT_TYPE_NOT_EXIST.getCode());
    }

    /**
     * 测试正常添加账户和用户信息
     */
    @Test
    void initialCreditAccount() {
        InitializeCreditAccountRequest initializeCreditAccountRequest = new InitializeCreditAccountRequest();
        initializeCreditAccountRequest.setIdCardNo("1234567");
        initializeCreditAccountRequest.setUsername("zzl");
        initializeCreditAccountRequest.setAmountType("1001");
        initializeCreditAccountRequest.setAddress("浙江省杭州市");
        ServiceResult<Void> serviceResult = creditAccountService.initialCreditAccount(initializeCreditAccountRequest);
        assert serviceResult.isSuccess();
    }

    /**
     * 测试账户已经申请
     */
    @Test
    void initialCreditAccountForExist() {
        InitializeCreditAccountRequest initializeCreditAccountRequest = new InitializeCreditAccountRequest();
        initializeCreditAccountRequest.setIdCardNo("1234567");
        initializeCreditAccountRequest.setUsername("zzl");
        initializeCreditAccountRequest.setAmountType("1001");
        initializeCreditAccountRequest.setAddress("浙江省杭州市");
        ServiceResult<Void> serviceResult = creditAccountService.initialCreditAccount(initializeCreditAccountRequest);
        assert serviceResult.getErrorCode().equals(CreditManagementErrCodeEnum.ALREADY_APPLY_ACCOUNT_AMOUNT.getCode());
    }

    /**
     * 测试增加额度
     */
    @Test
    void increaseCreditAccount() {
        // 查询增加额度之前账户
        ServiceResult<CreditAccount> creditAccountBefore = creditAccountService.queryCreditAccount("1234567");
        String idCardNo = creditAccountBefore.getResult().getIdCardNo();

        // 增加账户额度
        CreditAccountRequest creditAccountRequest = new CreditAccountRequest();
        creditAccountRequest.setIdCardNo(idCardNo);
        creditAccountRequest.setAmount(new BigDecimal(1000.00));
        creditAccountService.increaseCreditAccount(creditAccountRequest);

        // 查询增加之后账户额度
        ServiceResult<CreditAccount> creditAccountAfter = creditAccountService.queryCreditAccount(idCardNo);

        assert creditAccountBefore.getResult().getCreditLimit().add(new BigDecimal(1000.00)).equals(creditAccountAfter.getResult().getCreditLimit());
    }

    /**
     * 正常减少额度
     */
    @Test
    void decreaseCreditAccount() {
        // 查询减少额度之前账户
        ServiceResult<CreditAccount> creditAccountBefore = creditAccountService.queryCreditAccount("1234567");
        String idCardNo = creditAccountBefore.getResult().getIdCardNo();

        // 减少账户额度
        CreditAccountRequest creditAccountRequest = new CreditAccountRequest();
        creditAccountRequest.setIdCardNo(idCardNo);
        creditAccountRequest.setAmount(new BigDecimal(2000.00));
        creditAccountService.decreaseCreditAccount(creditAccountRequest);

        // 查询减少之后账户额度
        ServiceResult<CreditAccount> creditAccountAfter = creditAccountService.queryCreditAccount(idCardNo);
        assert creditAccountBefore.getResult().getCreditLimit().subtract(new BigDecimal(2000.00)).equals(creditAccountAfter.getResult().getCreditLimit());
    }

    /**
     * 测试额度不够扣减
     */
    @Test
    void decreaseCreditAccountAmountNotEnough() {
        // 查询减少额度之前账户
        ServiceResult<CreditAccount> creditAccountBefore = creditAccountService.queryCreditAccount("1234567");
        String idCardNo = creditAccountBefore.getResult().getIdCardNo();

        // 减少账户额度
        CreditAccountRequest creditAccountRequest = new CreditAccountRequest();
        creditAccountRequest.setIdCardNo(idCardNo);
        creditAccountRequest.setAmount(new BigDecimal(8000.00));
        ServiceResult<Void> serviceResult = creditAccountService.decreaseCreditAccount(creditAccountRequest);

        ServiceResult<CreditAccount> creditAccountAfter = creditAccountService.queryCreditAccount("1234567");
        // 抛出异常，并且额度仍然是之前的额度
        assert serviceResult.getErrorCode().equals(CreditManagementErrCodeEnum.CREDIT_ACCOUNT_AMOUNT_NOT_ENOUGH.getCode())
                && creditAccountBefore.getResult().getCreditLimit().equals(creditAccountAfter.getResult().getCreditLimit());

    }

    /**
     * 查询不存在的信用账户信息
     */
    @Test
    void queryCreditAccountNotExist() {
        String idCardNo = "-1";
        ServiceResult<CreditAccount> creditAccountServiceResult = creditAccountService.queryCreditAccount(idCardNo);

        assert Objects.isNull(creditAccountServiceResult.getResult());

    }

    /**
     * 查询存在的信用账户信息
     */
    @Test
    void queryCreditAccountExist() {
        String idCardNo = "1234567";
        ServiceResult<CreditAccount> creditAccountServiceResult = creditAccountService.queryCreditAccount(idCardNo);
        assert Objects.nonNull(creditAccountServiceResult.getResult());
    }
}