package com.compulynx.student_management.users.models;

public class SetPasswordRequest {
    private String resetCode;
    private String password;

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
