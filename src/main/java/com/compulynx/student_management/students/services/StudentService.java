package com.compulynx.student_management.students.services;

import com.compulynx.student_management.shared.models.ResponseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author Stanley Mungai
 */
@Service
public interface StudentService {
    ResponseEntity<ResponseModel> findAll(String studentClass, int page, int size);

    ResponseEntity<ResponseModel> findAll(int page, int size);

    ResponseEntity<ResponseModel> findAll(Long studentId, String studentClass, String startDate, String endDate, int page, int size);

    ResponseEntity<ResponseModel> findByStudentId(Long studentId);

    ResponseEntity<ResponseModel> findAll(String startDate, String endDate, int page, int size);

    Long count();
}
