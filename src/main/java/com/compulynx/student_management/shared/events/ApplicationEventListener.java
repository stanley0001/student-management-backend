package com.compulynx.student_management.shared.events;


import com.compulynx.student_management.communication.EmailService;
import com.compulynx.student_management.shared.events.appevents.AuthEvent;
import com.compulynx.student_management.shared.events.appevents.EmailEvent;
import com.compulynx.student_management.users.entities.Users;
import com.compulynx.student_management.users.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.Optional;

@EnableAsync
@Component
public class ApplicationEventListener {
    private final EmailService emailService;
    private final UserService userService;
    @Value("${application.auth.allowed-attempts}")
    private String allowedAuthAttempts;
    private static final Logger log = LoggerFactory.getLogger(ApplicationEventListener.class);

    public ApplicationEventListener(EmailService emailService, UserService userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    @EventListener
    @Async
    public void processEvent(EmailEvent event){
        log.info("email event received {}",event.getEmailData().toString());
        emailService.sendEmail(event.getEmailData());
    }

    @EventListener
    @Async
    public  void processEvent(AuthEvent event){
        log.info("Auth event received {}",event.toString());
        Optional<Users> usersOptional=userService.findTopByEmailIgnoreCase(event.getUsername());
        if (usersOptional.isEmpty()){
            log.warn("user not found by the given username {}",event.getUsername());
            return;
        }
        Users user=usersOptional.get();
        int loginAttempts=user.getLoginAttempts();
        switch (event.getStatus()){
            case "SUCCESS":
                loginAttempts=0;
                break;
            case "FAILED":
                loginAttempts++;
                if (loginAttempts>Integer.parseInt(allowedAuthAttempts))
                    user.setLocked(true);
                break;
            default:
                log.warn("logic not implemented for auth status: {}",event.getStatus());
        }
        user.setLoginAttempts(loginAttempts);
        userService.save(user);
    }
}
