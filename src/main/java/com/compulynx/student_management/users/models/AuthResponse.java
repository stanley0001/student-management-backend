package com.compulynx.student_management.users.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private Boolean authenticated;
    private String message;
    private String token;
    private int expiry;

    public AuthResponse(boolean b, String message) {
        this.authenticated=b;
        this.message=message;
    }

    public AuthResponse(String message) {
        this.message=message;
    }
    public AuthResponse() {

    }

    public AuthResponse(String jwtToken, int i) {
        this.authenticated=true;
        this.message="User authenticated";
        this.token=jwtToken;
        this.expiry=i;
    }

    public Boolean getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getExpiry() {
        return expiry;
    }

    public void setExpiry(int expiry) {
        this.expiry = expiry;
    }
}
