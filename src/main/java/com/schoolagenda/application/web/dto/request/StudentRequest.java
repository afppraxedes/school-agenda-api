// src/main/java/com/schoolagenda/application/web/dto/StudentRequest.java
package com.schoolagenda.application.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

public class StudentRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @NotBlank(message = "Class name is required")
    private String className;

    private String profilePhoto;

    // Constructors
    public StudentRequest() {}

    public StudentRequest(String fullName, LocalDate birthDate, String className, String profilePhoto) {
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.className = className;
        this.profilePhoto = profilePhoto;
    }

    // Getters and Setters
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
}