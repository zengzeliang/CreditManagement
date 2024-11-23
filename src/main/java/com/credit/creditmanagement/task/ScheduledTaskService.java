package com.credit.creditmanagement.task;

import com.credit.creditmanagement.bean.CreditAccountRequest;
import com.credit.creditmanagement.bean.InitializeCreditAccountRequest;
import com.credit.creditmanagement.service.CreditAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ScheduledTaskService {
    @Autowired
    private CreditAccountService creditAccountService;

    // 每天19点15分执行模拟多用户操作任务（可根据需求修改cron表达式改变定时时间）
    @Scheduled(cron = "0 15 19 * * *")
    public void simulateMultiUserOperations() {
        /**
         * 随机生成n请求
         */
        int n = 100;
        List<InitializeCreditAccountRequest> initializeCreditAccountRequestList = generateInitializeCreditAccountRequest(n);
        Random random = new Random();
        for (InitializeCreditAccountRequest initializeCreditAccountRequest : initializeCreditAccountRequestList) {
            // 随机进行额度初始化、增加或扣减操作
            int operationType = random.nextInt(3);
            if (operationType == 0) {
                creditAccountService.initialCreditAccount(initializeCreditAccountRequest);
            } else if (operationType == 1) {
                CreditAccountRequest creditAccountRequest = new CreditAccountRequest();
                creditAccountRequest.setIdCardNo(initializeCreditAccountRequest.getIdCardNo());
                double deductAmount = random.nextDouble() * 500; // 随机生成扣减额度
                creditAccountRequest.setAmount(new BigDecimal(deductAmount));
                creditAccountService.increaseCreditAccount(creditAccountRequest);
            } else {
                CreditAccountRequest creditAccountRequest = new CreditAccountRequest();
                creditAccountRequest.setIdCardNo(initializeCreditAccountRequest.getIdCardNo());
                double deductAmount = random.nextDouble() * 500; // 随机生成扣减额度
                creditAccountRequest.setAmount(new BigDecimal(deductAmount));
                creditAccountService.decreaseCreditAccount(creditAccountRequest);
            }
        }
    }

    private List<InitializeCreditAccountRequest> generateInitializeCreditAccountRequest(int n) {
        Random random = new Random();
        // 模拟idCardNo 为 1 - 10
        List<InitializeCreditAccountRequest> requests = new ArrayList<>();
        for(int i = 0; i < n; i++) {
            InitializeCreditAccountRequest initializeCreditAccountRequest = new InitializeCreditAccountRequest();
            initializeCreditAccountRequest.setAddress("address-xxx" + i);
            initializeCreditAccountRequest.setUsername("username-xxx" + i);
            initializeCreditAccountRequest.setAmountType("1001");
            initializeCreditAccountRequest.setIdCardNo(String.valueOf(random.nextInt(10) + 1));
            requests.add(initializeCreditAccountRequest);
        }
        return requests;
    }
}