package com.credit.creditmanagement.enums;

public enum AccountOperationTypeEnum {
    INITIAL_AMOUNT("initial_amount"),
    INCREASE_AMOUNT("increase_amount"),
    DECREASE_AMOUNT("decrease_amount"),
    ;

    AccountOperationTypeEnum(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationType() {
        return operationType;
    }

    private String operationType;
}
