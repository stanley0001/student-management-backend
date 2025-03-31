package com.compulynx.student_management.communication.implementations;


import com.compulynx.student_management.communication.EmailService;
import freemarker.template.Configuration;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final Configuration configuration;
    @Value("${spring.mail.username}")
    String username;
    @Value("${spring.mail.from}")
    String mailFrom;
    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    public EmailServiceImpl(JavaMailSender mailSender, Configuration configuration) {
        this.mailSender = mailSender;
        this.configuration = configuration;
    }

    String getEmailContent(HashMap<String,String> email){
        try {
            StringWriter stringWriter = new StringWriter();
            Map<String, Object> model = new HashMap<>();
            model.put("params",email);
            model.put("year",String.valueOf(LocalDate.now().getYear()));
            log.info("model {}",model);
            configuration.getTemplate("email.html").process(model, stringWriter);
            return stringWriter.getBuffer().toString();
        }catch (Exception e){
            log.error("ERROR transforming template: {}",e.getMessage());
        }
        return null;
    }

    public void sendEmail(HashMap<String,String> email){
        MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        try {
            helper.setSubject(email.get("SUBJECT").concat("@"+ String.valueOf(LocalDateTime.now().getHour()).concat(":"+String.valueOf(LocalDateTime.now().getMinute()))));
//            email.put("header",email.get("TEMPLATE").toUpperCase());
            email.remove("SUBJECT");
            if (email.get("BCC")!=null)
                helper.setBcc(email.get("BCC").split(";"));
            if (email.get("CC")!=null)
                helper.setCc(email.get("CC").split(";"));
            helper.setTo(InternetAddress.parse(email.get("RECIPIENT")));
            helper.setFrom(mailFrom);
        } catch (MessagingException e) {
            log.error("Error setting email params : {}",e.getMessage());
        }
        try {
            log.info("sending email to {}",email.get("RECIPIENT"));
            email.remove("RECIPIENT");
            //TODO:use custom templates
            email.remove("TEMPLATE");
            String emailContent = getEmailContent(email);
            email.remove("TEMPLATE");
            helper.setText(emailContent, true);
            mailSender.send(mimeMessage);
            log.info("Email sent");
        }catch (Exception e){
            log.error("send email error encountered : {}",e.getMessage());
        }
    }
}