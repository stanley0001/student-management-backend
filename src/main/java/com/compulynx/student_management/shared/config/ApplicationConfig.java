package com.compulynx.student_management.shared.config;

import com.compulynx.student_management.users.entities.Users;
import com.compulynx.student_management.users.repositories.UsersRepository;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.Optional;

@Configuration
public class ApplicationConfig {

    private final UsersRepository usersRepository;
    private static final Logger log = LoggerFactory.getLogger(ApplicationConfig.class);

    @Autowired
    public ApplicationConfig(@Lazy UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return username -> this.loadUser(username)
                .orElseThrow(()->new UsernameNotFoundException("User not found"));
    }
    private Optional<Users> loadUser(String username){
        log.info("loading users {}",username);
        Optional<Users> optionalUser=usersRepository.findTopByEmailIgnoreCase(username);
        if (optionalUser.isPresent())
            return optionalUser;
        log.info("loading users... {}",username);
        optionalUser=usersRepository.findTopByUsernameIgnoreCase(username);
        return optionalUser;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider=new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme bearerToken = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .in(SecurityScheme.In.HEADER)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("Authorization");
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("Bearer", bearerToken))
                .info(new Info().title("Student Management Backend service")
                        .description("Generation of data, file upload and manage students")
                        .version("2.0"))
                .addSecurityItem(new SecurityRequirement().addList("Bearer"));
    }

    @Bean
    public FreeMarkerConfigurer freemarkerConfig() {
        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setTemplateLoaderPath("classpath:/templates/");
        return freeMarkerConfigurer;
    }

}
