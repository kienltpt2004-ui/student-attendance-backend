package com.example.cdtn.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
//cam quyen khi student goi api admin