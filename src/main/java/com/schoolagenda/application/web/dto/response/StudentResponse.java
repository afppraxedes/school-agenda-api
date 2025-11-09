// src/main/java/com/schoolagenda/application/web/dto/response/StudentResponse.java
package com.schoolagenda.application.web.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StudentResponse {

    private Long id;
    private String fullName;
    private LocalDate birthDate;
    private String className;
    private String profilePhoto;
    private LocalDateTime registrationDate;
    private int age;

    public StudentResponse() {}

    public StudentResponse(Long id, String fullName, LocalDate birthDate, String className, String profilePhoto, LocalDateTime registrationDate, int age) {
        this.id = id;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.className = className;
        this.profilePhoto = profilePhoto;
        this.registrationDate = registrationDate;
        this.age = age;
    }

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}