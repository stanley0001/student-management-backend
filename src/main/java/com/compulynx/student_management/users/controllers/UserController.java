package com.compulynx.student_management.users.controllers;

import com.compulynx.student_management.shared.models.ResponseModel;
import com.compulynx.student_management.users.entities.Users;
import com.compulynx.student_management.users.models.User;
import com.compulynx.student_management.users.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v2/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('CAN_CREATE_USER')")
    @PostMapping
    public ResponseEntity<ResponseModel> createUser(@RequestBody User user){
        ResponseModel response = userService.createUser(user);
        return new ResponseEntity<>(response, response.getStatus());
    }
    @PreAuthorize("hasAuthority('CAN_UPDATE_USER')")
    @PutMapping
    public ResponseEntity<ResponseModel> updateUser(@RequestBody Users user){
        ResponseModel response=userService.updateUser(user);
        return new ResponseEntity<>(response,response.getStatus());
    }
    @PreAuthorize("hasAuthority('CAN_VIEW_USERS')")
    @GetMapping
    public ResponseEntity<ResponseModel> getUsers(@RequestParam(required = false,defaultValue = "0") int page,@RequestParam(required = false,defaultValue = "10") int size,@RequestParam(required = false,name = "firstname") String name,@RequestParam(required = false,name = "role") String role,@RequestParam(required = false,name = "id")  UUID id,@RequestParam(required = false,name = "isClient",defaultValue = "false")  boolean isClient){
        ResponseModel response;
        if (id!=null){
            response= userService.findById(id);
            return new ResponseEntity<>(response,response.getStatus());
        }
        if(name!=null) {
            response = userService.findByName(name,page,size);
            return new ResponseEntity<>(response,response.getStatus());
        }
        if(role!=null) {
            response = userService.findByRole(role,page,size);
            return new ResponseEntity<>(response,response.getStatus());
        }
        response=userService.getAll(page,size);
        return new ResponseEntity<>(response,response.getStatus());
    }
    @PreAuthorize("hasAuthority('CAN_CHANGE_USER_STATUS')")
    @GetMapping("/change-status/{id}")
    public ResponseEntity<ResponseModel> changeUserStatus(@PathVariable UUID id){
        ResponseModel response= userService.findById(id);
        return new ResponseEntity<>(response,response.getStatus());
    }
    @PreAuthorize("hasAuthority('CAN_UNLOCK_USER')")
    @GetMapping("/unlock/{id}")
    public ResponseEntity<ResponseModel> unlockUser(@PathVariable UUID id){
        ResponseModel response= userService.unlock(id);
        return new ResponseEntity<>(response,response.getStatus());
    }
}
