package com.compulynx.student_management.users.services.implementations;

import com.compulynx.student_management.shared.events.appevents.AuthEvent;
import com.compulynx.student_management.shared.events.appevents.EmailEvent;
import com.compulynx.student_management.shared.models.ResponseModel;
import com.compulynx.student_management.shared.services.JWTService;
import com.compulynx.student_management.users.entities.Privileges;
import com.compulynx.student_management.users.entities.Roles;
import com.compulynx.student_management.users.entities.Users;
import com.compulynx.student_management.users.models.AuthRequest;
import com.compulynx.student_management.users.models.AuthResponse;
import com.compulynx.student_management.users.repositories.PrivilegesRepository;
import com.compulynx.student_management.users.repositories.RolesRepository;
import com.compulynx.student_management.users.repositories.UsersRepository;
import com.compulynx.student_management.users.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
    public final AuthenticationManager authenticationManager;
    public final UserServiceImpl userDetailsService;
    private final RolesRepository rolesRepository;
    private final UsersRepository usersRepository;
    private final PrivilegesRepository privilegesRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final ApplicationEventPublisher eventPublisher;
    @Value("${application.base-url}")
    private String baseUrl;
    @Value("${application.auth.mfa-enabled}")
    private Boolean mfaEnabled;
    @Value("${application.auth.mfa-channel}")
    private String mfaChannel;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserServiceImpl userDetailsService, RolesRepository rolesRepository, UsersRepository usersRepository, PrivilegesRepository privilegesRepository, PasswordEncoder passwordEncoder, JWTService jwtService, ApplicationEventPublisher eventPublisher) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.rolesRepository = rolesRepository;
        this.usersRepository = usersRepository;
        this.privilegesRepository = privilegesRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.eventPublisher = eventPublisher;
    }
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private Map<String, Object> extractPrivilegesAsClaims(Users userDetails) {
        List<String> privilegeNames = userDetails.getRole().getPrivileges().stream()
                .map(Privileges::getName)
                .collect(Collectors.toList());
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", privilegeNames);
        return claims;
    }
    public ResponseModel authenticate(AuthRequest request){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserName(),request.getPassword()
                    )
            );
        }catch (Exception e){
            eventPublisher.publishEvent(new AuthEvent(this,request.getUserName(),"FAILED"));
            log.error("Auth error {}",e.getMessage());
            return new ResponseModel(e.getMessage(),HttpStatus.UNAUTHORIZED);
        }
        //get user
        Optional<Users> optionalUser=usersRepository.findTopByEmailIgnoreCase(request.getUserName());
        if (optionalUser.isEmpty()){
            optionalUser=usersRepository.findTopByUsernameIgnoreCase(request.getUserName());
            if (optionalUser.isEmpty())
                return new ResponseModel("User not found by the given username",HttpStatus.BAD_REQUEST);
        }
        Users user=optionalUser.get();
        if(mfaEnabled){
            if (request.getOtp()!=null){
                //TODO:generate otp
                switch (mfaChannel.toUpperCase()){
                    case "EMAIL":
                        //fire email event
                        break;
                    case "SMS":
                        //fire SMS event
                        break;
                    case "AUTHENTICATOR":
                        //fire authenticator event
                    default:
                        log.warn("MFA channel {} not implemented",mfaChannel);
                }
                return new ResponseModel("OTP generated",HttpStatus.OK);
            }
            //TODO:validate otp
            boolean validOtp=false;
            boolean otpExpired=true;
            if (!validOtp || otpExpired){
                log.warn(validOtp ? "OTP expired" : "Invalid OTP provided");
                eventPublisher.publishEvent(new AuthEvent(this,request.getUserName(),"FAILED"));
                return new ResponseModel(validOtp ?"Expired OTP":"Invalid OTP provided",HttpStatus.BAD_REQUEST);
            }
        }
        //generate token
        eventPublisher.publishEvent(new AuthEvent(this,request.getUserName(),"SUCCESS"));
        var jwtToken=jwtService.generateToken(extractPrivilegesAsClaims(user),user);
        AuthResponse authResponse=new AuthResponse(jwtToken,60*60);
        return  new ResponseModel(authResponse.getMessage(),HttpStatus.OK,authResponse);
    }

    @Override
    public ResponseModel passwordReset(String email) {
        Optional<Users> optionalUser=usersRepository.findTopByEmailIgnoreCase(email);
        if (optionalUser.isEmpty()) {
            log.warn("User not found by email {} on password rest request", email);
            return new ResponseModel("User not found by the given email",HttpStatus.BAD_REQUEST);
        }
        Users user=optionalUser.get();
        user.generateCoreRelator();
        user=usersRepository.save(user);
        HashMap<String, String> emailData = getStringStringHashMap(user);
        this.eventPublisher.publishEvent(new EmailEvent(this,emailData));
        return new ResponseModel("A reset link has been sent to your email",HttpStatus.OK);
    }

    private HashMap<String, String> getStringStringHashMap(Users user) {
        HashMap<String,String> emailData=new HashMap<>();
        emailData.put("SUBJECT","PASSWORD RESET");
        emailData.put("TEMPLATE","AUTH");
        emailData.put("RECIPIENT", user.getEmail());
        emailData.put("url",baseUrl.concat(user.getCoreRelator()));
        //TODO:replace with a template engine
        emailData.put("message","Dear "+ user.getName()+"\n We received a password reset request on your account, please click the below link to setup your password\n Please reach out if you did not initiate this");
        return emailData;
    }

    @Override
    public List<Roles> getAllRoles() {
        //paginate
        return rolesRepository.findAll();
    }

    public Roles getRole(Long id){
        return rolesRepository.findById(id).orElse(null);
    }

    @Override
    public Roles createRole(Roles role) {
        return rolesRepository.save(role);
    }

    @Override
    public List<Privileges> getAllPermissions() {
        //paginate
        return privilegesRepository.findAll();
    }

    @Override
    public Privileges createPermission(Privileges permission) {
        return privilegesRepository.save(permission);
    }
    public List<Privileges> createDefaultPrivileges(){
        List<Privileges> privilegesList=new Privileges().init();
        return privilegesRepository.saveAll(privilegesList);
    }
    public Optional<Roles> getRoleByName(String name){
        return rolesRepository.findByName(name);
    }
    public ResponseModel getProfileByUserName(String username){
        Optional<Users> optionalUser=usersRepository.findTopByEmailIgnoreCase(username);
        return optionalUser.map(users -> new ResponseModel("profile found", HttpStatus.OK, users)).orElseGet(() -> new ResponseModel("User not found", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Override
    public ResponseModel changePassword(String code, String password) {
        Optional<Users> optionalUser=usersRepository.findTopByCoreRelator(code);
        if (optionalUser.isEmpty())
            return new ResponseModel("Provided code not found", HttpStatus.BAD_REQUEST);
        Users user=optionalUser.get();
        if (user.isCoreRelatorExpired())
            return new ResponseModel("Provided code is expired", HttpStatus.BAD_REQUEST);
        try {
            user.setPassword(passwordEncoder.encode(password));
            user.setEnabled(true);
            //TODO: remove unlock at password reset
//            if (!user.isAccountNonLocked())
//               user.setLocked(false);
            user.setCoreRelatorExpiresAt(LocalDateTime.now());
            usersRepository.save(user);
            return new ResponseModel("Password set successfully, Please login", HttpStatus.OK);
        }catch (Exception e){
            log.error("ERROR changing password {}",e.getMessage());
            return new ResponseModel(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

