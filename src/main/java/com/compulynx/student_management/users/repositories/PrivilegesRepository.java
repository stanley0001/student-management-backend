package com.compulynx.student_management.users.repositories;

import com.compulynx.student_management.users.entities.Privileges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface PrivilegesRepository extends JpaRepository<Privileges, Long> {
}
