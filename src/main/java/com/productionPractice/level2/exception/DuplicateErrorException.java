package com.productionPractice.level2.exception;

public class DuplicateErrorException extends RuntimeException{
    public DuplicateErrorException(String message)
    {
       super(message);
    }
}
