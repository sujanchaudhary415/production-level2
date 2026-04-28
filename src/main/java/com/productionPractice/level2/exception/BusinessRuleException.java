package com.productionPractice.level2.exception;

public class BusinessRuleException extends RuntimeException{
    public BusinessRuleException(String message)
    {
        super(message);
    }
}
