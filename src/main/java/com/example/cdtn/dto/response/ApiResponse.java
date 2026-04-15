package com.example.cdtn.dto.response;

import com.example.cdtn.entity.enums.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;

@JsonPropertyOrder({ "message", "status", "detail", "data", "metaData", "timestamp" })
public class ApiResponse<T> {

    private String message;
    private Status status;
    private String detail;
    private LocalDateTime timestamp;
    private T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MetaData metaData;

    public ApiResponse(String message, Status status, String detail, T data){
        this.message = message;
        this.status = status;
        this.detail = detail;
        this.timestamp = LocalDateTime.now();
        this.data = data;
    }
    public ApiResponse(String message, Status status, String detail, T data, MetaData metaData){
        this.message = message;
        this.status = status;
        this.detail = detail;
        this.timestamp = LocalDateTime.now();
        this.data = data;
        this.metaData = metaData;
    }


    public int getStatus(){
        return status.getCode();
    }

    public T getData(){
        return data;
    }

    public String getMessage() {
        return message;
    }

    public String getDetail() {
        return detail;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }
}
