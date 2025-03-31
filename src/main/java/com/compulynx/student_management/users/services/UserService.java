package com.compulynx.student_management.users.services;

import com.compulynx.student_management.shared.models.ResponseModel;
import com.compulynx.student_management.users.entities.Users;
import com.compulynx.student_management.users.models.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    ResponseModel createUser(User user);
    ResponseModel updateUser(Users user);
    ResponseModel getAll(int page, int size);
    List<Users> getAll();
    ResponseModel findByName(String name,int page, int size);
    ResponseModel findByRole(String role, int page, int size);
    ResponseModel findById(UUID id);

    Optional<Users> findTopByEmailIgnoreCase(String username);

    void save(Users user);

    ResponseModel unlock(UUID id);
}
