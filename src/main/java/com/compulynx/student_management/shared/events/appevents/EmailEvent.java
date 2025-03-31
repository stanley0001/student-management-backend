package com.compulynx.student_management.shared.events.appevents;

import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
public class EmailEvent extends ApplicationEvent {
    private final HashMap<String,String> emailData;

    public EmailEvent(Object source, HashMap<String, String> emailData) {
        super(source);
        this.emailData = emailData;
    }

    public HashMap<String, String> getEmailData() {
        return emailData;
    }
}
