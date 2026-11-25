package com.digitalwallet.common.error;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.List;

public class ApiException extends RuntimeException{

    private final HttpStatus status;
    private final String code;
    private final String title;
    private final List<FieldErrorDetail> errors;
    private final HttpHeaders headers;

    public ApiException(HttpStatus status, String code, String title, String detail) {
        this(status, code, title, detail, null, null);
    }

    public ApiException(HttpStatus status, String code, String title, String detail, List<FieldErrorDetail> errors, HttpHeaders headers) {
        super(detail);
        this.status = status;
        this.code = code;
        this.title = title;
        this.errors = errors;
        this.headers = headers;
    }
    
    public HttpStatus getStatus() { return status; }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public List<FieldErrorDetail> getErrors() { return errors; }
    public HttpHeaders getHeaders() { return headers; }
}
