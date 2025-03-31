package com.compulynx.student_management.users.repositories;


import com.compulynx.student_management.users.entities.Roles;
import com.compulynx.student_management.users.entities.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<Users, UUID> {
    Optional<Users> findTopByEmailIgnoreCase(String email);
    Optional<Users> findTopByCoreRelator(String coreRelator);

    List<Users> findAllByFirstName(String name);

    List<Users> findAllByRole(Roles roles);

    Optional<Users> findByEmailIgnoreCase(String name);

    Optional<Users> findTopByUsernameIgnoreCase(String email);
}
