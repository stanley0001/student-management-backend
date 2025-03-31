package com.compulynx.student_management.users.controllers;

import com.compulynx.student_management.shared.models.ResponseModel;
import com.compulynx.student_management.users.entities.Privileges;
import com.compulynx.student_management.users.entities.Roles;
import com.compulynx.student_management.users.models.AuthRequest;
import com.compulynx.student_management.users.models.SetPasswordRequest;
import com.compulynx.student_management.users.services.AuthService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequestMapping("api/v2/auth")
public class AuthController {
    @Autowired
    AuthService authService;
    @PostMapping("authenticate")
    public ResponseEntity<ResponseModel> authenticate(@RequestBody AuthRequest request){
        ResponseModel response= authService.authenticate(request);
        return new ResponseEntity<>(response,response.getStatus());
    }
    @GetMapping("profile")
    public ResponseEntity<ResponseModel> getProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return new ResponseEntity<>(new ResponseModel("profile found",HttpStatus.OK,userDetails),HttpStatus.OK);
        }
        assert authentication != null;
        ResponseModel response=authService.getProfileByUserName(authentication.getPrincipal().toString());
        return new ResponseEntity<>(response,response.getStatus());
    }

    @GetMapping("/password-reset")
    public ResponseEntity<ResponseModel> resetPassword(@RequestParam String email){
        ResponseModel response= authService.passwordReset(email);
        return new ResponseEntity<>(response,response.getStatus());
    }
    @PostMapping("/password-change")
    public ResponseEntity<ResponseModel> changePassword(@RequestBody SetPasswordRequest request){
        ResponseModel response= authService.changePassword(request.getResetCode(),request.getPassword());
        return new ResponseEntity<>(response,response.getStatus());
    }
    @PreAuthorize("hasAuthority('CAN_VIEW_ROLE')")
    @GetMapping("/roles")
    public ResponseEntity<List<Roles>> getAllRoles(@RequestParam(required = false,defaultValue = "0", name = "page") int page, @RequestParam(required = false,defaultValue = "10", name = "size") int size){
        List<Roles> roles = authService.getAllRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }
    @PreAuthorize("hasAuthority('CAN_VIEW_ROLE')")
    @GetMapping("/roles/{id}")
    public ResponseEntity<Roles> getRole(@PathVariable("id") Long id){
        Roles roles = authService.getRole(id);
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }
    @PreAuthorize("hasAuthority('CAN_CREATE_ROLE')")
    @PostMapping("/roles")
    public ResponseEntity<Roles> createRole(@RequestBody Roles role){
        Roles roles = authService.createRole(role);
        return new ResponseEntity<>(roles,HttpStatus.CREATED);
    }
    @PreAuthorize("hasAuthority('CAN_VIEW_PRIVILEGES')")
    @GetMapping("/permissions")
    public ResponseEntity<List<Privileges>> getAllPermissions(){
        List<Privileges> permissions = authService.getAllPermissions();
        return new ResponseEntity<>(permissions, HttpStatus.OK);
    }
    @PreAuthorize("hasAuthority('CAN_CREATE_PRIVILEGE')")
    @PostMapping("/permissions")
    public ResponseEntity<Privileges> createPermission(@RequestBody Privileges permission){
        Privileges createdPermissions = authService.createPermission(permission);
        return new ResponseEntity<>(createdPermissions,HttpStatus.CREATED);
    }
}
