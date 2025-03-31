package com.compulynx.student_management.users.services;

import com.compulynx.student_management.shared.models.ResponseModel;
import com.compulynx.student_management.users.entities.Privileges;
import com.compulynx.student_management.users.entities.Roles;
import com.compulynx.student_management.users.models.AuthRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface AuthService {
    ResponseModel authenticate(AuthRequest request);

    ResponseModel passwordReset(String email);

    List<Roles> getAllRoles();

    Roles createRole(Roles role);

    List<Privileges> getAllPermissions();

    Privileges createPermission(Privileges permission);

    ResponseModel changePassword(String code, String password);

    List<Privileges> createDefaultPrivileges();

    Optional<Roles> getRoleByName(String superAdmin);

    ResponseModel getProfileByUserName(String username);

    Roles getRole(Long id);
}
