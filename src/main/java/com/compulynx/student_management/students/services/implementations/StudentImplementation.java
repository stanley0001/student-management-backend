package com.compulynx.student_management.students.services.implementations;

import com.compulynx.student_management.shared.models.ResponseModel;
import com.compulynx.student_management.students.entities.Student;
import com.compulynx.student_management.students.repositories.StudentRepository;
import com.compulynx.student_management.students.services.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Stanley Mungai
 */
@Service
public class StudentImplementation implements StudentService {
    private final StudentRepository studentRepository;

    public StudentImplementation(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public ResponseEntity<ResponseModel> findAll(String studentClass, int page, int size) {
        Pageable pageable= PageRequest.of(page,size);
        Page<Student> studentPage=studentRepository.findAllByStudentClass(studentClass,pageable);
        ResponseModel response=new ResponseModel(studentPage.getTotalElements()<1?"No elements found":studentPage.getTotalElements()+" elements found", HttpStatus.OK,studentPage.get(),page,size,studentPage.getTotalElements(),studentPage.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ResponseModel> findAll(int page, int size) {
        Pageable pageable= PageRequest.of(page,size);
        Page<Student> studentPage=studentRepository.findAll(pageable);
        ResponseModel response=new ResponseModel(studentPage.getTotalElements()<1?"No elements found":studentPage.getTotalElements()+" elements found", HttpStatus.OK,studentPage.get(),page,size,studentPage.getTotalElements(),studentPage.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ResponseModel> findAll(Long studentId, String studentClass, String startDate, String endDate, int page, int size) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseModel> findByStudentId(Long studentId) {
        Optional<Student> studentOptional =studentRepository.findById(studentId);
        return studentOptional.map(student -> ResponseEntity.ok(new ResponseModel("Data retried successfully", HttpStatus.OK, student))).orElseGet(() -> ResponseEntity.ok(new ResponseModel("Data not found by the given id: " + studentId, HttpStatus.NOT_FOUND)));
    }

    @Override
    public ResponseEntity<ResponseModel> findAll(String startDate, String endDate, int page, int size) {
        Pageable pageable= PageRequest.of(page,size);
//        Page<Student> studentPage=studentRepository.findAllByDOB(startDate,endDate,pageable);
        Page<Student> studentPage = studentRepository.findAllByDOBBetween(startDate, endDate, pageable);
        ResponseModel response=new ResponseModel(studentPage.getTotalElements()<1?"No elements found":studentPage.getTotalElements()+"elements found", HttpStatus.OK,studentPage.get(),page,size,studentPage.getTotalElements(),studentPage.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @Override
    public Long count() {
        return studentRepository.count();
    }

}
