// src/main/java/com/university/models/Student.java
package com.university.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private String rollNumber;
    private LocalDate admissionDate;
    private int currentSemester;
    private double cgpa;
    private int totalCredits;
    private List<Course> enrolledCourses;

    public Student() {
        super();
        this.enrolledCourses = new ArrayList<>();
    }

    public Student(int userId, String uniqueId, String fullName, String email, String password,
                   String phone, String address, LocalDate dateOfBirth, String gender,
                   String rollNumber, LocalDate admissionDate, int currentSemester, double cgpa) {
        super(userId, uniqueId, fullName, email, password, "student", phone, address, dateOfBirth, gender);
        this.rollNumber = rollNumber;
        this.admissionDate = admissionDate;
        this.currentSemester = currentSemester;
        this.cgpa = cgpa;
        this.totalCredits = 0;
        this.enrolledCourses = new ArrayList<>();
    }

    // Getters and Setters
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public LocalDate getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(LocalDate admissionDate) { this.admissionDate = admissionDate; }

    public int getCurrentSemester() { return currentSemester; }
    public void setCurrentSemester(int currentSemester) { this.currentSemester = currentSemester; }

    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }

    public int getTotalCredits() { return totalCredits; }
    public void setTotalCredits(int totalCredits) { this.totalCredits = totalCredits; }

    public List<Course> getEnrolledCourses() { return enrolledCourses; }
    public void setEnrolledCourses(List<Course> enrolledCourses) { this.enrolledCourses = enrolledCourses; }
}