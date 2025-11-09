package com.schoolagenda.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "class_name", nullable = false)
    private String className;

    @Column(name = "profile_photo")
    private String profilePhoto;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    // Constructors
    public Student() {
        this.registrationDate = LocalDateTime.now();
    }

    public Student(String fullName, LocalDate birthDate, String className, String profilePhoto) {
        this();
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.className = className;
        this.profilePhoto = profilePhoto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    // Utility method to calculate age
    public int getAge() {
        return LocalDate.now().getYear() - birthDate.getYear();
    }
}
