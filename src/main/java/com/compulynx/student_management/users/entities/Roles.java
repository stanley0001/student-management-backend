package com.compulynx.student_management.users.entities;

import com.compulynx.student_management.shared.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
public class Roles extends BaseEntity {
    @Id
    @Column(nullable = false,unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private String name;
    private String description;
    private String roleType;
    private String roleStatus;
    @ManyToMany(fetch =FetchType.EAGER)
    private List<Privileges> privileges =new ArrayList<>();

    public Roles() {
    }

    public Roles(String superAdmin, String superAdmin1, List<Privileges> defaultPrivileges) {
           this.name=superAdmin;
           this.description=superAdmin1;
           this.privileges=defaultPrivileges;
           this.roleStatus="ACTIVE";
           this.roleType="ADMINISTRATOR";
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getRoleStatus() {
        return roleStatus;
    }

    public void setRoleStatus(String roleStatus) {
        this.roleStatus = roleStatus;
    }

    public List<Privileges> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<Privileges> privileges) {
        this.privileges = privileges;
    }
}
