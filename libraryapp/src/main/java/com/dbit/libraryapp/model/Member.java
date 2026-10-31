package com.dbit.libraryapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "members", uniqueConstraints = {
        @UniqueConstraint(name = "uk_members_code", columnNames = "member_code"),
        @UniqueConstraint(name = "uk_members_email", columnNames = "email")
})
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_code", nullable = false, length = 30)
    private String memberCode;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 160)
    private String email;

    @Column(nullable = false, length = 30)
    private String phone;

    @Column(nullable = false)
    private LocalDate registrationDate;

    @PrePersist
    void beforeCreate() {
        if (registrationDate == null) registrationDate = LocalDate.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMemberCode() { return memberCode; }
    public void setMemberCode(String memberCode) { this.memberCode = memberCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
}
