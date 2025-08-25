package com.example.finance_control.exceptions;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("Email already exists: "+email);
    }
}
