package com.compulynx.student_management.shared.events;


import com.compulynx.student_management.communication.EmailService;
import com.compulynx.student_management.files.services.ExcelService;
import com.compulynx.student_management.shared.events.appevents.AuthEvent;
import com.compulynx.student_management.shared.events.appevents.EmailEvent;
import com.compulynx.student_management.shared.events.appevents.FileProcessesEvent;
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
    private final ExcelService excelService;
    @Value("${application.auth.allowed-attempts}")
    private String allowedAuthAttempts;
    @Value("${spring.file.path}")
    private String basePath;
    private static final Logger log = LoggerFactory.getLogger(ApplicationEventListener.class);

    public ApplicationEventListener(EmailService emailService, UserService userService, ExcelService excelService) {
        this.emailService = emailService;
        this.userService = userService;
        this.excelService = excelService;
    }

    @EventListener
    @Async
    public void processEvent(EmailEvent event){
        log.info("email event received {}",event.getEmailData().toString());
        emailService.sendEmail(event.getEmailData());
    }

    @EventListener
    @Async
    public void processEvent(FileProcessesEvent event){
        log.info("file process: {} event received",event.getProcess().name());
        switch (event.getProcess()){
            case GENERATE -> {
                log.info("schedule file generation with {} records",event.getValue());
                try {
                    excelService.generateStudentBatchData(event.getValue());
                    log.info("{} records generation completed successfully",event.getValue());
                }catch (Exception e){
                    log.error("Error generating scheduled file: {}",e.getMessage());
                }
            }
            case PROCESS -> {
                log.info("schedule file processing with id {}",event.getValue());
                //TODO:get file paths from process using process is
                String excelFilePath = this.basePath.concat("dataprocessing/students.xlsx");
                String csvFilePath = this.basePath.concat("dataprocessing/students.csv");
                try {
                    excelService.processExcelToCSV(excelFilePath,csvFilePath);
                    log.info("file processing schedule success");
                }catch (Exception e){
                    log.error("Error processing scheduled file: {}",e.getMessage());
                }
            }
            case UPLOAD -> {
                log.info("schedule file upload with id {}",event.getValue());
                String excelFilePath = this.basePath.concat("dataprocessing/students.xlsx");
                try {
                    excelService.uploadFileData(excelFilePath);
                    log.info("file upload schedule success");
                }catch (Exception e){
                    log.error("Error uploading scheduled file: {}",e.getMessage());
                }
            }
            default -> {
                log.warn("{} logic not implemented",event.getProcess().name());
            }
        }

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
