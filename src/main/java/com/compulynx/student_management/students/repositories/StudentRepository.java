package com.compulynx.student_management.students.repositories;

import com.compulynx.student_management.students.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Stanley Mungai
 */

@Repository
public interface StudentRepository extends JpaRepository<Student,Long> {
    Page<Student> findAllByClass(String studentClass, Pageable pageable);

    Page<Student> findAllByDOB(String startDate, String endDate, Pageable pageable);

    Page<Student> findAllByDOBBetween(String startDate, String endDate, Pageable pageable);

    Page<Student> findAllByStudentClass(String studentClass, Pageable pageable);
}
