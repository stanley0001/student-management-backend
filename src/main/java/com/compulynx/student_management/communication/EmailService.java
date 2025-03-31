package com.compulynx.student_management.communication;

import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public interface EmailService {
    void sendEmail(HashMap<String,String> email);
}
