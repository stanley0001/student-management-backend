package com.compulynx.student_management.shared.events.appevents;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
public class AuthEvent extends ApplicationEvent {
    private final String username;
    private final String status;

    public AuthEvent(Object source,String username,String status){
        super(source);
        this.username=username;
        this.status=status;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }
}
