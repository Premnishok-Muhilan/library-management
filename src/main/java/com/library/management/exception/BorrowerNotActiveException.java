package com.library.management.exception;

public class BorrowerNotActiveException extends RuntimeException {
    public BorrowerNotActiveException(String message) {
        super(message);
    }
}
