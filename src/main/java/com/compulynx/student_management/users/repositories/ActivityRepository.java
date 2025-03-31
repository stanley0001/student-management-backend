package com.compulynx.student_management.users.repositories;

import com.compulynx.student_management.users.entities.ActivityLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ActivityRepository extends JpaRepository<ActivityLogs, Long> {
}
