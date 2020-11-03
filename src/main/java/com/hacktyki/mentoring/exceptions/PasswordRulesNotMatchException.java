package com.hacktyki.mentoring.exceptions;

public class PasswordRulesNotMatchException extends RuntimeException {

    public PasswordRulesNotMatchException(String message) {
        super(message);
    }
}
