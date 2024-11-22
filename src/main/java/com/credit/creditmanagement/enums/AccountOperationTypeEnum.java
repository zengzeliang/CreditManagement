package com.credit.creditmanagement.enums;

public enum AccountOperationTypeEnum {
    INITIAL_AMOUNT("initial_amount"),
    ADD_AMOUNT("add_amount"),
    SUBTRACT_AMOUNT("subtract_amount"),
    ;

    AccountOperationTypeEnum(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationType() {
        return operationType;
    }

    private String operationType;
}
