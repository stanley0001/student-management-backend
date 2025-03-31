package com.compulynx.student_management.shared.components;

import com.compulynx.student_management.communication.implementations.EmailServiceImpl;
import com.compulynx.student_management.users.entities.Privileges;
import com.compulynx.student_management.users.entities.Roles;
import com.compulynx.student_management.users.entities.Users;
import com.compulynx.student_management.users.models.User;
import com.compulynx.student_management.users.services.AuthService;
import com.compulynx.student_management.users.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component

public class AppInitializer {
    private final UserService userService;
    private final AuthService authService;
    @Value("${application.auth.default-user.first-name}")
    private String defaultFirstName;
    @Value("${application.auth.default-user.last-name}")
    private String defaultLastName;
    @Value("${application.auth.default-user.email}")
    private String defaultEmail;
    @Value("${application.auth.default-user.phone}")
    private String defaultPhone;
    @Value("${application.auth.default-role}")
    private String defaultRoleName;
    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    public AppInitializer(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    private List<Privileges> getDefaultPrivileges(){
        List<Privileges> privileges=authService.getAllPermissions();
        if (!privileges.isEmpty())
            return privileges;
        return authService.createDefaultPrivileges();
    }
    private void getDefaultRole(){
        List<Roles> roles=authService.getAllRoles();
        if (!roles.isEmpty())return;
        Roles role=new Roles(defaultRoleName,defaultRoleName,getDefaultPrivileges());
        authService.createRole(role);
    }
    private void getDefaultUser(){
        //create default user
        List<Users> users= userService.getAll();
        if (!users.isEmpty())return;
        User defaultUser=new User(defaultFirstName,defaultLastName,defaultEmail,defaultPhone,defaultRoleName);
        userService.createUser(defaultUser);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initialize(){
        log.info("Running initialization ...");
        this.getDefaultRole();
        this.getDefaultUser();
        log.info("initialization done.");
    }
}
