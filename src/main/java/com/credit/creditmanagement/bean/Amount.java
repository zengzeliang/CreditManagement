package com.credit.creditmanagement.bean;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额类
 */
public class Amount {
    /**
     * 额度
     */
    private BigDecimal balance;

    public Amount() {
        this.balance = BigDecimal.ZERO;
    }
    public void add(BigDecimal amount) {
        balance = balance.add(amount);
    }
    public void subtract(BigDecimal amount) {
        balance = balance.subtract(amount);
    }
    public BigDecimal getBalance() {
        return balance.setScale(2, RoundingMode.HALF_UP);
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
