// src/main/java/com/university/models/Teacher.java
package com.university.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Teacher extends User {
    private String employeeId;
    private LocalDate hireDate;
    private String qualification;
    private String specialization;
    private boolean isHod;
    private List<Course> courses;

    public Teacher() {
        super();
        this.courses = new ArrayList<>();
        this.isHod = false;
    }

    public Teacher(int userId, String uniqueId, String fullName, String email, String password,
                   String phone, String address, LocalDate dateOfBirth, String gender,
                   String employeeId, LocalDate hireDate, String qualification,
                   String specialization, boolean isHod) {
        super(userId, uniqueId, fullName, email, password, "teacher", phone, address, dateOfBirth, gender);
        this.employeeId = employeeId;
        this.hireDate = hireDate;
        this.qualification = qualification;
        this.specialization = specialization;
        this.isHod = isHod;
        this.courses = new ArrayList<>();
    }

    // Getters and Setters
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public boolean isHod() { return isHod; }
    public void setHod(boolean hod) { isHod = hod; }

    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
}