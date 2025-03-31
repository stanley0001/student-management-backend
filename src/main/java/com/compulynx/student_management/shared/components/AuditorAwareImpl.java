package com.compulynx.student_management.shared.components;

import com.compulynx.student_management.shared.events.ApplicationEventListener;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<String> {
    public static AuditorAwareImpl instance;

    public AuditorAwareImpl() {
        instance=this;
    }
    private static final Logger log = LoggerFactory.getLogger(AuditorAwareImpl.class);

    @Override
    public  Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
           userDetails.getUsername();
        }
        try {
            if ((authentication != null ? authentication.getPrincipal() : null) !=null){
                UserDetails user=(UserDetails)authentication.getPrincipal();
                return Optional.of(user.getUsername());
            }
        }catch (Exception e){
            log.warn("error getting user {}",e.getMessage());
        }
        return Optional.of("sys-user");
    }
}

