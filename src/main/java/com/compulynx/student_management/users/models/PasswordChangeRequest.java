package com.compulynx.student_management.users.models;

import lombok.Data;

import java.util.UUID;

@Data
public class PasswordChangeRequest {
    private UUID userId;
    private String newPassword;
}
