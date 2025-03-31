package com.compulynx.student_management.users.models;

import com.compulynx.student_management.users.entities.Roles;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public class User {
    private UUID id;
    private String name;
    private String firstName;
    private String lastName;
    private String otherName;
    private String email;
    @JsonIgnore
    private Roles role;
    private String roleName;
    private String phone;
    private String Status;
    private Boolean locked=false;
    private Boolean enabled=false;
    public User(String name, String lastname, String mail, String number,String defaultRole) {
        this.firstName=name;
        this.lastName=lastname;
        this.email=mail;
        this.phone=number;
        this.roleName=defaultRole;
    }

    public User() {

    }

    public String getStatus(){
        return !enabled?"DISABLED":locked?"LOCKED":"ACTIVE";
    }
    public String getName(){
        if (firstName==null)firstName="";
        return firstName.concat(" "+lastName+" "+otherName);
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
