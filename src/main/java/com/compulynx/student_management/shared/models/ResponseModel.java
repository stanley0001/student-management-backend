package com.compulynx.student_management.shared.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseModel {
    private String message;
    private String error;
    private HttpStatus status;
    private transient Object data;
    private Integer page;
    private Integer size;
    private Integer totalPages;
    private Integer totalElements;

    public ResponseModel(String message, HttpStatus httpStatus) {
        this.message=message;
        this.status=httpStatus;
    }
    public ResponseModel(String message, HttpStatus httpStatus,Object o) {
        this.message=message;
        this.status=httpStatus;
        this.data=o;
    }

    public ResponseModel(String message, HttpStatus httpStatus, Object o, int page, int size) {
        this.message=message;
        this.status=httpStatus;
        this.data=o;
        this.page=page;
        this.size=size;
    }
    public ResponseModel(String message, HttpStatus httpStatus, Object o, int page, int size, long totalElements,int totalPages) {
        this.message=message;
        this.status=httpStatus;
        this.data=o;
        this.page=page;
        this.size=size;
        this.totalElements=(int) totalElements;
        this.totalPages=totalPages;
    }

    public ResponseModel(String usersFound, HttpStatus httpStatus, Object o, int page, int size, int size1) {
        this.message=message;
        this.status=httpStatus;
        this.data=o;
        this.page=page;
        this.size=size;
        totalElements=size1;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Integer totalElements) {
        this.totalElements = totalElements;
    }
}
