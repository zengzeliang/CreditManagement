package com.credit.creditmanagement.service.impl;

import com.credit.creditmanagement.bean.CreditAccountRequest;
import com.credit.creditmanagement.bean.InitializeCreditAccountRequest;
import com.credit.creditmanagement.bean.ServiceResult;
import com.credit.creditmanagement.entity.*;
import com.credit.creditmanagement.enums.AccountOperationTypeEnum;
import com.credit.creditmanagement.enums.CreditManagementErrCodeEnum;
import com.credit.creditmanagement.enums.TransLogStatusEnum;
import com.credit.creditmanagement.exception.CreditManagementException;
import com.credit.creditmanagement.mapper.CreditAccountMapper;
import com.credit.creditmanagement.mapper.CreditAccountTransactionLogMapper;
import com.credit.creditmanagement.mapper.SeqNoMapper;
import com.credit.creditmanagement.mapper.UserInfoMapper;
import com.credit.creditmanagement.service.AmountCategoryService;
import com.credit.creditmanagement.service.CreditAccountService;
import com.credit.creditmanagement.util.DateTimeUtil;
import com.mysql.cj.util.StringUtils;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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

    @Autowired
    private ApplicationContext applicationContext;

    private static Integer seqNoLen = 5;

    /**
     * 初始化信用账户额度
     * @param request
     * @return
     */
    @Override
    public ServiceResult<Void> initialCreditAccount(InitializeCreditAccountRequest request) {
        if(Objects.isNull(request) || StringUtil.isNullOrEmpty(request.getAmountType()) || StringUtil.isNullOrEmpty(request.getIdCardNo())) {
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
            // 保存流水记录
            CreditAccountTransactionLog creditAccountTransactionLog = saveTransLog(AccountOperationTypeEnum.INITIAL_AMOUNT, TransLogStatusEnum.START_TRANS_LOG);
            // 给用户开户以及更新流水状态
            // 获取spring中给事务添加切面的对象，否则不会通过带切面的对象执行，导致事务失效
            CreditAccountServiceImpl creditAccountService = applicationContext.getBean(CreditAccountServiceImpl.class);
            creditAccountService.saveCreditAccountAmount(request, amount, creditAccountTransactionLog);
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
            // 发送mq异步更新流水表为失败状态，不影响主流程
            // sendMqForTransLogUpdateFial();
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
     * 给账户增加额度
     * @param request
     * @return
     */
    @Override
    @Transactional
    public ServiceResult<Void> increaseCreditAccount(CreditAccountRequest request) {
        if (Objects.isNull(request) || StringUtils.isNullOrEmpty(request.getIdCardNo()) || Objects.isNull(request.getAmount())) {
            return ServiceResult.getFailureResult(CreditManagementErrCodeEnum.EMPTY_PARAM.getCode(), CreditManagementErrCodeEnum.EMPTY_PARAM.getMessage());
        }
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ServiceResult.getFailureResult(CreditManagementErrCodeEnum.PARAM_ERROR.getCode(), CreditManagementErrCodeEnum.PARAM_ERROR.getMessage());
        }

        try {
            // 记录流水表
            CreditAccountTransactionLog creditAccountTransactionLog = saveTransLog(AccountOperationTypeEnum.INCREASE_AMOUNT, TransLogStatusEnum.START_TRANS_LOG);
            // 查询额度记录
            CreditAccount creditAccount = creditAccountMapper.selectCreditAccountByIdCardNoForUpdate(request.getIdCardNo());
            // 不存在记录，需要先申请额度
            if (Objects.isNull(creditAccount)) {
                throw new CreditManagementException(CreditManagementErrCodeEnum.CREDIT_ACCOUNT_AMOUNT_NOT_EXIST.getCode(), CreditManagementErrCodeEnum.CREDIT_ACCOUNT_AMOUNT_NOT_EXIST.getMessage());
            }
            BigDecimal creditLimit = creditAccount.getCreditLimit();
            BigDecimal addMount = creditLimit.add(request.getAmount());
            updateCreditAccountAmount(creditAccount, addMount, creditAccountTransactionLog);
            return ServiceResult.getSuccessResult(null);
        } catch (Throwable e) {
            log.error("CreditAccountController increaseCreditAccount error", e);
            // 发送mq异步更新流水表为失败状态，不影响主流程
            // sendMqForTransLogUpdateFial();
            // 为自定义异常，抛出自定义错误信息
            if (e instanceof  CreditManagementException) {
                CreditManagementException exception = (CreditManagementException) e;
                return ServiceResult.getFailureResult(exception.getErrorCode(), exception.getMessage());
            }
            // 其余异常
            return ServiceResult.getFailureResult(CreditManagementErrCodeEnum.INCREASE_CREDIT_ACCOUNT_ERROR.getCode(), CreditManagementErrCodeEnum.INCREASE_CREDIT_ACCOUNT_ERROR.getMessage());
        }
    }

    /**
     * 扣减额度
     * @param request
     * @return
     */
    @Override
    public ServiceResult<Void> decreaseCreditAccount(CreditAccountRequest request) {
        if (Objects.isNull(request) || StringUtils.isNullOrEmpty(request.getIdCardNo()) || Objects.isNull(request.getAmount())) {
            return ServiceResult.getFailureResult(CreditManagementErrCodeEnum.EMPTY_PARAM.getCode(), CreditManagementErrCodeEnum.EMPTY_PARAM.getMessage());
        }
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ServiceResult.getFailureResult(CreditManagementErrCodeEnum.PARAM_ERROR.getCode(), CreditManagementErrCodeEnum.PARAM_ERROR.getMessage());
        }
        try {
            // 记录流水表
            CreditAccountTransactionLog creditAccountTransactionLog = saveTransLog(AccountOperationTypeEnum.DECREASE_AMOUNT, TransLogStatusEnum.START_TRANS_LOG);
            // 查询额度记录
            CreditAccount creditAccount = creditAccountMapper.selectCreditAccountByIdCardNoForUpdate(request.getIdCardNo());
            // 不存在记录，需要先申请额度
            if (Objects.isNull(creditAccount)) {
                throw new CreditManagementException(CreditManagementErrCodeEnum.CREDIT_ACCOUNT_AMOUNT_NOT_EXIST.getCode(), CreditManagementErrCodeEnum.CREDIT_ACCOUNT_AMOUNT_NOT_EXIST.getMessage());
            }
            BigDecimal creditLimit = creditAccount.getCreditLimit();
            BigDecimal addMount = creditLimit.subtract(request.getAmount());

            if (addMount.compareTo(BigDecimal.ZERO) < 0) {
                // 扣减后额度 < 0，表示额度不够
                throw new CreditManagementException(CreditManagementErrCodeEnum.CREDIT_ACCOUNT_AMOUNT_NOT_ENOUGH.getCode(), CreditManagementErrCodeEnum.CREDIT_ACCOUNT_AMOUNT_NOT_ENOUGH.getMessage());
             }
            updateCreditAccountAmount(creditAccount, addMount, creditAccountTransactionLog);
            return ServiceResult.getSuccessResult(null);
        } catch (Throwable e) {
            log.error("CreditAccountController decreaseCreditAccount error", e);
            // 发送mq异步更新流水表为失败状态，不影响主流程
            // sendMqForTransLogUpdateFial();
            // 为自定义异常，抛出自定义错误信息
            if (e instanceof  CreditManagementException) {
                CreditManagementException exception = (CreditManagementException) e;
                return ServiceResult.getFailureResult(exception.getErrorCode(), exception.getMessage());
            }
            // 其余异常
            return ServiceResult.getFailureResult(CreditManagementErrCodeEnum.DECREASE_CREDIT_ACCOUNT_ERROR.getCode(), CreditManagementErrCodeEnum.DECREASE_CREDIT_ACCOUNT_ERROR.getMessage());
        }
    }

    /**
     * 查询账户额度
     * @param idCardNo
     * @return
     */
    @Override
    public ServiceResult<CreditAccount> queryCreditAccount(String idCardNo) {
        if (StringUtil.isNullOrEmpty(idCardNo)) {
            return ServiceResult.getFailureResult(CreditManagementErrCodeEnum.EMPTY_PARAM.getCode(), CreditManagementErrCodeEnum.EMPTY_PARAM.getMessage());
        }
        try {
            // 查询额度记录
            CreditAccount creditAccount = creditAccountMapper.selectCreditAccountByIdCardNo(idCardNo);
            return ServiceResult.getSuccessResult(creditAccount);
        } catch (Throwable e) {
            log.error("CreditAccountController queryCreditAccount error", e);
            // 发送mq异步更新流水表为失败状态，不影响主流程
            // sendMqForTransLogUpdateFial();
            // 为自定义异常，抛出自定义错误信息
            if (e instanceof  CreditManagementException) {
                CreditManagementException exception = (CreditManagementException) e;
                return ServiceResult.getFailureResult(exception.getErrorCode(), exception.getMessage());
            }
            // 其余异常
            return ServiceResult.getFailureResult(CreditManagementErrCodeEnum.QUERY_CREDIT_ACCOUNT_ERROR.getCode(), CreditManagementErrCodeEnum.QUERY_CREDIT_ACCOUNT_ERROR.getMessage());
        }
    }

    /**
     * 保存流水记录
     * @param accountOperationTypeEnum
     * @param transLogStatusEnum
     * @return
     */
    @Transactional
    public CreditAccountTransactionLog saveTransLog(AccountOperationTypeEnum accountOperationTypeEnum, TransLogStatusEnum transLogStatusEnum) {
        CreditAccountTransactionLog creditAccountTransactionLog = new CreditAccountTransactionLog();
        SeqNo seqNo = new SeqNo();
        seqNo.setValue(null);
        // 长期序列号考虑序列中心获取，一次缓存n个序列号到内存
        int insertSeqNoRes = seqNoMapper.insertSeqNo(seqNo);
        if (insertSeqNoRes < 1) {
            log.error("saveTransLog insertSeqNo error");
            throw new CreditManagementException(CreditManagementErrCodeEnum.OBTAIN_SEQ_NO_ERROR.getCode(), CreditManagementErrCodeEnum.OBTAIN_SEQ_NO_ERROR.getMessage());
        }
        // 不足n位补0
        String seqNoFormat = String.format("%0" + seqNoLen + "d", seqNo.getId());
        creditAccountTransactionLog.setSeqNo(DateTimeUtil.getCurTime() +  seqNoFormat);
        creditAccountTransactionLog.setOperationTime(new Date());
        creditAccountTransactionLog.setStatus(transLogStatusEnum.getStatus());
        creditAccountTransactionLog.setOperation(accountOperationTypeEnum.getOperationType());
        int insertTransactionLogRes = creditAccountTransactionLogMapper.insertCreditAccountTransactionLog(creditAccountTransactionLog);
        if (insertTransactionLogRes < 1) {
            log.error("saveTransLog insertCreditAccountTransactionLog error");
            throw new CreditManagementException(CreditManagementErrCodeEnum.INSERT_CREDIT_ACCOUNT_AMOUNT_TRANSACTION_LOG_ERROR.getCode(), CreditManagementErrCodeEnum.INSERT_CREDIT_ACCOUNT_AMOUNT_TRANSACTION_LOG_ERROR.getMessage());
        }
        return creditAccountTransactionLog;
    }

    /**
     * 初始化额度信息，事务原子操作，要么同时执行成功，要么同时失败
     * @param request
     * @param amount
     * @return
     */
    @Transactional
    public void saveCreditAccountAmount(InitializeCreditAccountRequest request, BigDecimal amount, CreditAccountTransactionLog creditAccountTransactionLog) {
        // 生成信息账户初始额度信息
        CreditAccount creditAccount = new CreditAccount();
        creditAccount.setIdCardNo(request.getIdCardNo());
        creditAccount.setCreditLimit(amount);
        int insertCreditAccountRes = creditAccountMapper.insertCreditAccount(creditAccount);
        if (insertCreditAccountRes < 1) {
            log.error("saveCreditAccountAmount insertCreditAccount error");
            throw new CreditManagementException(CreditManagementErrCodeEnum.INSERT_CREDIT_ACCOUNT_AMOUNT_ERROR.getCode(), CreditManagementErrCodeEnum.INSERT_CREDIT_ACCOUNT_AMOUNT_ERROR.getMessage());
        }
        // 更新流水状态
        CreditAccountTransactionLog updateTransactionLog = new CreditAccountTransactionLog();
        updateTransactionLog.setCreditAccountId(creditAccount.getId());
        updateTransactionLog.setStatus(TransLogStatusEnum.SUCCESS.getStatus());
        updateTransactionLog.setSeqNo(creditAccountTransactionLog.getSeqNo());
        int updateTransactionLogRes = creditAccountTransactionLogMapper.updateCreditAccountTransactionLog(updateTransactionLog);
        if (updateTransactionLogRes < 1) {
            log.error("saveCreditAccountAmount updateCreditAccountTransactionLog error");
            throw new CreditManagementException(CreditManagementErrCodeEnum.UPDATE_CREDIT_ACCOUNT_AMOUNT_TRANSACTION_LOG_ERROR.getCode(), CreditManagementErrCodeEnum.UPDATE_CREDIT_ACCOUNT_AMOUNT_TRANSACTION_LOG_ERROR.getMessage());
        }
    }

    /**
     * 新增额度信息，事务原子操作，要么同时执行成功，要么同时失败
     * @param creditAccount
     * @param amount
     * @param creditAccountTransactionLog
     */
    @Transactional
    public void updateCreditAccountAmount(CreditAccount creditAccount, BigDecimal amount, CreditAccountTransactionLog creditAccountTransactionLog) {
        // 设置新的额度信息
        int updateCreditAccountAmountRes = creditAccountMapper.updateCreditAccountAmountById(creditAccount.getId(), amount);
        if (updateCreditAccountAmountRes < 1) {
            log.error("updateCreditAccountAmount updateCreditAccountAmountById error");
            throw new CreditManagementException(CreditManagementErrCodeEnum.UPDATE_CREDIT_ACCOUNT_AMOUNT_ERROR.getCode(), CreditManagementErrCodeEnum.UPDATE_CREDIT_ACCOUNT_AMOUNT_ERROR.getMessage());
        }
        // 更新流水状态
        CreditAccountTransactionLog updateTransactionLog = new CreditAccountTransactionLog();
        updateTransactionLog.setCreditAccountId(creditAccount.getId());
        updateTransactionLog.setStatus(TransLogStatusEnum.SUCCESS.getStatus());
        updateTransactionLog.setSeqNo(creditAccountTransactionLog.getSeqNo());
        int updateTransactionLogRes = creditAccountTransactionLogMapper.updateCreditAccountTransactionLog(updateTransactionLog);
        if (updateTransactionLogRes < 1) {
            log.error("updateCreditAccountAmount updateCreditAccountTransactionLog error");
            throw new CreditManagementException(CreditManagementErrCodeEnum.UPDATE_CREDIT_ACCOUNT_AMOUNT_TRANSACTION_LOG_ERROR.getCode(), CreditManagementErrCodeEnum.UPDATE_CREDIT_ACCOUNT_AMOUNT_TRANSACTION_LOG_ERROR.getMessage());
        }
    }
}
