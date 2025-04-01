package com.compulynx.student_management.students.controllers;

import com.compulynx.student_management.shared.enums.StudentClasses;
import com.compulynx.student_management.shared.models.ResponseModel;
import com.compulynx.student_management.students.services.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Stanley Mungai
 */
@RestController
@RequestMapping("/api/v2/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<ResponseModel> getStudents(
            @RequestParam(name = "studentId", required = false) Long studentId,
            @RequestParam(name = "studentClass", required = false) String studentClass,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        if (studentId!=null)
            return studentService.findByStudentId(studentId);
        if (studentClass!=null)
            return studentService.findAll(studentClass,page,size);
        if (startDate!=null && endDate!=null)
            return studentService.findAll(startDate,endDate,page,size);
        return studentService.findAll(page,size);
    }
    @GetMapping("classes")
    public ResponseEntity<ResponseModel> getStudentClasses(){
        List<String> studentClasses = Arrays.stream(StudentClasses.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ResponseModel("Data retried", HttpStatus.OK,studentClasses));
    }
    @GetMapping("count")
    public ResponseEntity<ResponseModel> getStudentCount(){
        Long count=studentService.count();
        return ResponseEntity.ok(new ResponseModel("Data retried", HttpStatus.OK,count));
    }

}
