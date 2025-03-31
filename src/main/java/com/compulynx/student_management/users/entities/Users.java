package com.compulynx.student_management.users.entities;

import com.compulynx.student_management.shared.entities.BaseEntity;
import com.compulynx.student_management.users.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Builder
@Entity
@Table(name = "portal_users")
public class Users extends BaseEntity implements UserDetails {
    @Id
    @Column(nullable = false,unique = true,updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @JsonIgnore
    private String coreRelator;
    private String firstName;
    private String lastName;
    private String otherName;
    @Column(unique = true)
    private String email;
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private Roles role;
    @Transient
    @JsonProperty("role")
    private String roleName;
    private String phone;
    @Transient
    private String status;
    @JsonIgnore
    private String password;
    private String username;
    private String documentNumber;
    @JsonIgnore
    private Integer loginAttempts=0;
    @JsonIgnore
    private Boolean active=true;
    @JsonIgnore
    private Boolean locked=false;
    @JsonIgnore
    private Boolean enabled=false;
    @JsonIgnore
    private LocalDateTime coreRelatorExpiresAt;
    @UpdateTimestamp
    @JsonIgnore
    private LocalDate passwordUpdatedAt;

    public Users() {
    }

    public Users(User request) {
        this.firstName=request.getFirstName();
        this.lastName=request.getLastName();
        this.otherName=request.getOtherName();
        this.email=request.getEmail();
        this.phone=request.getPhone();
        this.coreRelatorExpiresAt=LocalDateTime.now().plusDays(1);
    }

    public boolean isCoreRelatorExpired(){
        return this.coreRelatorExpiresAt == null || coreRelatorExpiresAt.isBefore(LocalDateTime.now());
    }
    public String getName(){
            if (firstName==null)firstName="";
            return firstName.concat(" "+lastName);
    }
    public String getStatus(){
        return !enabled?"DISABLED":locked?"LOCKED":"ACTIVE";
    }
    public void generateCoreRelator(){
        this.coreRelator= String.valueOf(UUID.randomUUID()).concat(id.toString());
        this.coreRelatorExpiresAt=LocalDateTime.now().plusDays(1);
    }
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.role.getPrivileges().stream().map(e->new SimpleGrantedAuthority(e.getValue())).toList();
    }
    public String getRoleName() {
        return role != null ? role.getName() :this.roleName!=null?this.roleName: "Not Assigned";
    }
    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
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

    public String getCoreRelator() {
        return coreRelator;
    }

    public void setCoreRelator(String coreRelator) {
        this.coreRelator = coreRelator;
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
        this.status = status;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(Integer loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public LocalDateTime getCoreRelatorExpiresAt() {
        return coreRelatorExpiresAt;
    }

    public void setCoreRelatorExpiresAt(LocalDateTime coreRelatorExpiresAt) {
        this.coreRelatorExpiresAt = coreRelatorExpiresAt;
    }

    public LocalDate getPasswordUpdatedAt() {
        return passwordUpdatedAt;
    }

    public void setPasswordUpdatedAt(LocalDate passwordUpdatedAt) {
        this.passwordUpdatedAt = passwordUpdatedAt;
    }
}
