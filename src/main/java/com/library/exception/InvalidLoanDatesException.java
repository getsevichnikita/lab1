package com.library.exception;

public class InvalidLoanDatesException extends RuntimeException {

    public InvalidLoanDatesException(String message) {
        super(message);
    }
}