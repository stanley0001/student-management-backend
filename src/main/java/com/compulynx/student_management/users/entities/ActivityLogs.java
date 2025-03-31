package com.compulynx.student_management.users.entities;

import com.compulynx.student_management.shared.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class ActivityLogs extends BaseEntity {
    @Id
    @Column(nullable = false,unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private Users actionBy;
    private String action;
    @Column(length = 1500)
    private String description;
}
