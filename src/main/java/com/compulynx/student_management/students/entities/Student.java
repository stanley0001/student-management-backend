package com.compulynx.student_management.students.entities;

import com.compulynx.student_management.shared.entities.BaseEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

/**
 * @author Stanley Mungai
 */

@Entity
public class Student extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String studentId;
    private String firstName;
    private String lastName;
    private String DOB;
    @JsonProperty("class")
    @Column(name = "class")
    private String studentClass;
    private String score;
    private String status;
    private String photoPath;

    public Student() {
    }

    public Student(String studentId, String firstName, String lastName, String DOB, String studentClass, String score, String status, String photoPath) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.DOB = DOB;
        this.studentClass = studentClass;
        this.score = score;
        this.status = status;
        this.photoPath = photoPath;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
