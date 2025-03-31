package com.compulynx.student_management.users.models;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private String email;
    private String reason;
}
