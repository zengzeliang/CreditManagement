package com.credit.creditmanagement.service.impl;

import com.credit.creditmanagement.bean.InitializeCreditAccountRequest;
import com.credit.creditmanagement.bean.ServiceResult;
import com.credit.creditmanagement.entity.*;
import com.credit.creditmanagement.enums.AccountOperationTypeEnum;
import com.credit.creditmanagement.enums.CreditManagementErrCodeEnum;
import com.credit.creditmanagement.exception.CreditManagementException;
import com.credit.creditmanagement.mapper.CreditAccountMapper;
import com.credit.creditmanagement.mapper.CreditAccountTransactionLogMapper;
import com.credit.creditmanagement.mapper.SeqNoMapper;
import com.credit.creditmanagement.mapper.UserInfoMapper;
import com.credit.creditmanagement.service.AmountCategoryService;
import com.credit.creditmanagement.service.CreditAccountService;
import com.credit.creditmanagement.util.DateTimeUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
public class CreditAccountServiceImpl implements CreditAccountService {

    @Autowired
    private AmountCategoryService amountCategoryService;

    @Autowired
    private CreditAccountMapper creditAccountMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private SeqNoMapper seqNoMapper;

    @Autowired
    private CreditAccountTransactionLogMapper creditAccountTransactionLogMapper;

    private static Integer seqNoLen = 5;

    /**
     * 初始化信用账户额度
     * @param request
     * @return
     */
    @Override
    public ServiceResult<Void> initialCreditAccount(InitializeCreditAccountRequest request) {
        if(request == null || StringUtil.isNullOrEmpty(request.getAmountType()) || StringUtil.isNullOrEmpty(request.getIdCardNo())) {
            return ServiceResult.getFailureResult(CreditManagementErrCodeEnum.EMPTY_PARAM.getCode(), CreditManagementErrCodeEnum.EMPTY_PARAM.getMessage());
        }
        try {
            // 如果已经申请成功了，不能再次申请
            CreditAccount creditAccount = creditAccountMapper.selectCreditAccountByIdCardNo(request.getIdCardNo());
            if (Objects.nonNull(creditAccount)) {
                throw new CreditManagementException(CreditManagementErrCodeEnum.ALREADY_APPLY_ACCOUNT_AMOUNT.getCode(), CreditManagementErrCodeEnum.ALREADY_APPLY_ACCOUNT_AMOUNT.getMessage());
            }
            // 根据类型查询额度
            AmountCategory amountCategory = amountCategoryService.queryAmountCategoryByType(request.getAmountType());
            if (Objects.isNull(amountCategory)) {
                throw new CreditManagementException(CreditManagementErrCodeEnum.AMOUNT_TYPE_NOT_EXIST.getCode(), CreditManagementErrCodeEnum.AMOUNT_TYPE_NOT_EXIST.getMessage());
            }
            // 获取申请的额度
            BigDecimal amount = amountCategory.getAmount();

            // 保存流水记录以及给用户开户
            saveCreditAccountAmount(request, amount);

            // 保存用户信息
            UserInfo userInfo = new UserInfo();
            userInfo.setUsername(request.getUsername());
            userInfo.setIdCardNo(request.getIdCardNo());
            userInfo.setAddress(request.getAddress());
            int insertUserInfoRes = userInfoMapper.insertUserInfo(userInfo);
            if (insertUserInfoRes < 1) {
                // 新增用户信息失败，后续考虑发mq等补偿添加用户信息逻辑，不影响主流程
                log.warn("CreditAccountServiceImpl initialCreditAccount insertUserInfo error");
            }
            return ServiceResult.getSuccessResult(null);
        } catch (Throwable e) {
            log.error("CreditAccountController initialCreditAccount error", e);
            // 为自定义异常，抛出自定义错误信息
            if (e instanceof  CreditManagementException) {
                CreditManagementException exception = (CreditManagementException) e;
                return ServiceResult.getFailureResult(exception.getErrorCode(), exception.getMessage());
            }
            // 其余异常
            return ServiceResult.getFailureResult(CreditManagementErrCodeEnum.INITIAL_CREDIT_ACCOUNT_ERROR.getCode(), CreditManagementErrCodeEnum.INITIAL_CREDIT_ACCOUNT_ERROR.getMessage());
        }
    }

    /**
     * 事务原子操作，要么同时执行成功，要么同时失败
     * @param request
     * @param amount
     * @return
     */
    @Transactional
    public void saveCreditAccountAmount(InitializeCreditAccountRequest request, BigDecimal amount) {
        // 生成信息账户初始额度信息
        CreditAccount creditAccount = new CreditAccount();
        creditAccount.setIdCardNo(request.getIdCardNo());
        creditAccount.setCreditLimit(amount);
        int insertCreditAccountRes = creditAccountMapper.insertCreditAccount(creditAccount);
        if (insertCreditAccountRes < 1) {
            log.error("saveCreditAccountAmount insertCreditAccount error");
            throw new CreditManagementException(CreditManagementErrCodeEnum.INSERT_CREDIT_ACCOUNT_AMOUNT_ERROR.getCode(), CreditManagementErrCodeEnum.INSERT_CREDIT_ACCOUNT_AMOUNT_ERROR.getMessage());
        }
        // 保存流水
        CreditAccountTransactionLog creditAccountTransactionLog = new CreditAccountTransactionLog();
        SeqNo seqNo = new SeqNo();
        seqNo.setValue(Strings.EMPTY);
        // 长期序列号考虑序列中心获取，一次缓存n个序列号到内存
        int insertSeqNoRes = seqNoMapper.insertSeqNo(seqNo);
        if (insertSeqNoRes < 1) {
            log.error("saveCreditAccountAmount insertSeqNo error");
            throw new CreditManagementException(CreditManagementErrCodeEnum.OBTAIN_SEQ_NO_ERROR.getCode(), CreditManagementErrCodeEnum.OBTAIN_SEQ_NO_ERROR.getMessage());
        }
        // 不足n位补0
        String seqNoFormat = String.format("%0" + seqNoLen + "d", seqNo.getId());
        creditAccountTransactionLog.setSeqNo(DateTimeUtil.getCurTime() +  seqNoFormat);
        creditAccountTransactionLog.setOperationTime(new Date());
        creditAccountTransactionLog.setCreditAccountId(creditAccount.getId());
        creditAccountTransactionLog.setOperation(AccountOperationTypeEnum.INITIAL_AMOUNT.getOperationType());
        int insertTransactionLogRes = creditAccountTransactionLogMapper.insertCreditAccountTransactionLog(creditAccountTransactionLog);
        if (insertTransactionLogRes < 1) {
            log.error("saveCreditAccountAmount insertCreditAccountTransactionLog error");
            throw new CreditManagementException(CreditManagementErrCodeEnum.INSERT_CREDIT_ACCOUNT_AMOUNT_TRANSACTION_LOG_ERROR.getCode(), CreditManagementErrCodeEnum.INSERT_CREDIT_ACCOUNT_AMOUNT_TRANSACTION_LOG_ERROR.getMessage());
        }
    }
}
