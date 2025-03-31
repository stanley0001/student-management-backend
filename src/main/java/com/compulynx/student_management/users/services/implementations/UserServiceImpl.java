package com.compulynx.student_management.users.services.implementations;
import com.compulynx.student_management.shared.events.appevents.EmailEvent;
import com.compulynx.student_management.shared.models.ResponseModel;
import com.compulynx.student_management.users.entities.Privileges;
import com.compulynx.student_management.users.entities.Roles;
import com.compulynx.student_management.users.entities.Users;
import com.compulynx.student_management.users.models.AuthResponse;
import com.compulynx.student_management.users.models.User;
import com.compulynx.student_management.users.repositories.PrivilegesRepository;
import com.compulynx.student_management.users.repositories.RolesRepository;
import com.compulynx.student_management.users.repositories.UsersRepository;
import com.compulynx.student_management.users.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    public final UsersRepository userRepo;
    public final RolesRepository roles;
    private final PrivilegesRepository privilegesRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${application.base-url}")
    private String baseUrl;

    public UserServiceImpl(UsersRepository userRepo, RolesRepository roles, PrivilegesRepository privilegesRepository, ApplicationEventPublisher eventPublisher, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roles = roles;
        this.privilegesRepository = privilegesRepository;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseModel createUser(User request) {
        log.info("Creating user: {}", request.getFirstName());
        Users user=new Users(request);
        Optional<Roles> role=this.getRoleByName(request.getRoleName());
        role.ifPresent(user::setRole);
        try {
            user=userRepo.save(user);
            user.generateCoreRelator();
            HashMap<String,String> emailData=new HashMap<>();
            emailData.put("SUBJECT","NEW USER CREATED");
            emailData.put("TEMPLATE","NEW USER CREATED");
            emailData.put("RECIPIENT",user.getEmail());
            emailData.put("url",baseUrl.concat(user.getCoreRelator()));
            //TODO:replace with a template engine
            emailData.put("message","Dear "+user.getName()+"\n You have been onboarded as a user in our administrative dashboard, please click the below link to setup your password, your username is "+user.getEmail());
            this.eventPublisher.publishEvent(new EmailEvent(this,emailData));
            userRepo.save(user);
            return new ResponseModel("User created", HttpStatus.CREATED);
        }catch (Exception e){
            log.error("ERROR saving user.. {}",e.getMessage());
            return new ResponseModel(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseModel updateUser(Users user) {
        log.info("Updating user: {}", user.getFirstName());
        Optional<Users> optionalUser=userRepo.findById(user.getId());
        if (optionalUser.isEmpty())
            return new ResponseModel("user does not exist with the given id",HttpStatus.BAD_REQUEST);
        //update role
        if (!optionalUser.get().getRoleName().equalsIgnoreCase(user.getRoleName())){
            Optional<Roles> roleToUpdate=roles.findByName(user.getRoleName());
            if (roleToUpdate.isPresent())user.setRole(roleToUpdate.get());
        }else
            user.setRole(optionalUser.get().getRole());
        user.setPassword(optionalUser.get().getPassword());
        user.setEnabled(optionalUser.get().getEnabled());
        user.setLocked(optionalUser.get().getLocked());
        user=userRepo.save(user);
        return new ResponseModel("user updated",HttpStatus.OK,user);
    }
    public ResponseModel getAll(int page, int size) {
        Pageable pageable= PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Users> usersPage=userRepo.findAll(pageable);
        return new ResponseModel(usersPage.getTotalElements()+" users found",HttpStatus.OK,usersPage.get(),page,size,usersPage.getTotalElements(),usersPage.getTotalPages());
    }
    public List<Users> getAll(){return userRepo.findAll();};

    @Override
    public ResponseModel findByName(String name,int page, int size) {
        List<Users> users=userRepo.findAllByFirstName(name);
        return new ResponseModel("users found",HttpStatus.OK,users,page,size,users.size());
    }
    public ResponseModel findByRole(String role, int page, int size){
        Optional<Roles> rolesOptional=getRoleByName(role);
        if (rolesOptional.isEmpty()){
            return new ResponseModel("Role not found by the given name:"+role,HttpStatus.NOT_FOUND);
        }
        List<Users> users=userRepo.findAllByRole(rolesOptional.get());
        return new ResponseModel("users found",HttpStatus.OK,users,page,size,users.size());
    }

    public Optional<Users> findByEmail(String email) {
        return userRepo.findTopByEmailIgnoreCase(email);
    }
    public ResponseModel findById(UUID id) {
        Optional<Users> optionalUser=userRepo.findById(id);
        return optionalUser.map(users -> new ResponseModel("success", HttpStatus.OK, users)).orElseGet(() -> new ResponseModel("User not found by the given id", HttpStatus.BAD_REQUEST));
    }

    @Override
    public Optional<Users> findTopByEmailIgnoreCase(String username) {
        return userRepo.findTopByEmailIgnoreCase(username);
    }

    @Override
    public void save(Users user) {
        userRepo.save(user);
    }

    @Override
    public ResponseModel unlock(UUID id) {
        Optional<Users> optionalUser=userRepo.findById(id);
        if (optionalUser.isEmpty())
            return new ResponseModel("User not found by the given id",HttpStatus.NOT_FOUND);
        Users user=optionalUser.get();
        user.setLocked(!user.getLocked());
        userRepo.save(user);
        return new ResponseModel(user.getLocked()?"User account locked":"User account unlocked",HttpStatus.OK);
    }

    // encoder
    public String encode(String plainText){

        return bCryptPasswordEncoder().encode(plainText);
    }
    //decoder
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    public String decode(String encodedText){
        return new String(Base64.getDecoder().decode(encodedText));
    }
    //Password reset
    public AuthResponse passwordReset(String email) {
        log.info("Reset password.");
        Optional<Users> user=userRepo.findTopByEmailIgnoreCase(email);

        return null;
    }
    public AuthResponse findUser(String name){
        return null;
    }
    //authenticate
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {

//        Optional<Users> user=userRepo.findTopByEmail(name);
        Optional<Users> user=userRepo.findByEmailIgnoreCase(name);

        if (user.isPresent()){
            log.info("Auth");
            Roles role=user.get().getRole();
            return null;

        }else{

            log.warn("User: {} Not found.", name);
            throw  new BadCredentialsException("user: "+name+" not found");
        }

    }




    public AuthResponse changePassword(String userId, String newPassword) {
        AuthResponse responseModel=new AuthResponse();
        log.info("change password");

        return responseModel;
    }

    //Roles and permissions
    public Roles createRole(Roles role){
        role.setCreatedAt(LocalDateTime.now());

        return roles.save(role);

    }

    public Roles updateRole(Roles role){
        return roles.save(role);
    }


    public List<Roles> getAllRoles(){ return roles.findAll(); }
    public Roles getRoleById(String id){
        return roles.findById(Long.parseLong(id)).get();
    }
    public Optional<Roles> getRoleByName(String name){
        return roles.findByName(name);
    }
    public Privileges createPermission(Privileges permission){
//        return permissions.save(permission);
      return null;
    }
    public List<Privileges> getAllPermissions(){
        return privilegesRepository.findAll();
    }

    public Optional<Roles> findRoleById(Long id){
        return roles.findById(id);
    }
    public Optional<Privileges> findPermissionById(Long id){
        return privilegesRepository.findById(id);
    }


}
