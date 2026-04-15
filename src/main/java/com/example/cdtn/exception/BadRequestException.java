package com.example.cdtn.exception;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message){
        super(message);
    }
}
//Dữ liệu sai logic (trùng email, trùng studentCode…)
