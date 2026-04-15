package com.example.cdtn.entity.enums;

public enum Status {

    SUCCESS(200),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    ERROR(500);

    private final int code;

    Status(int code){
        this.code = code;
    }

    public int getCode(){
        return code;
    }
}