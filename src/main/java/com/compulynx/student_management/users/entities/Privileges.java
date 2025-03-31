package com.compulynx.student_management.users.entities;

import com.compulynx.student_management.shared.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
public class Privileges extends BaseEntity {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String value;
    private String status;

    public Privileges(String name, String status) {
        this.name = name;
        this.value = name;
        this.status = status;
    }

    public Privileges() {

    }

    public List<Privileges> init() {
        List<String> privilegeNames = Arrays.asList(
                "CAN_VIEW_ROLE",
                "CAN_CREATE_ROLE",
                "CAN_VIEW_PRIVILEGES",
                "CAN_CREATE_PRIVILEGE",
                "CAN_VIEW_USER",
                "CAN_VIEW_USERS",
                "CAN_UPDATE_USER",
                "CAN_CREATE_USER",
                "CAN_CHANGE_USER_STATUS",
                "CAN_UNLOCK_USER"
                );

        List<Privileges> privilegesList = new ArrayList<>();
        String addedBy = "SYSTEM_INIT";
        String ipAddress = "127.0.0.1";

        for (String name : privilegeNames) {
            Privileges privilege = new Privileges(
                    name,
                    "ACTIVE"
            );
            privilegesList.add(privilege);
        }

        return privilegesList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
