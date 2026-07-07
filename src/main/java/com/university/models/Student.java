// src/main/java/com/university/models/Student.java
package com.university.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private String rollNumber;
    private int departmentId;
    private String departmentName;
    private LocalDate admissionDate;
    private int currentSemester;
    private double cgpa;
    private int totalCredits;
    private List<Course> enrolledCourses;
    private List<Enrollment> enrollments;
    private List<Attendance> attendances;
    private List<Fee> fees;

    public Student() {
        super();
        this.enrolledCourses = new ArrayList<>();
        this.enrollments = new ArrayList<>();
        this.attendances = new ArrayList<>();
        this.fees = new ArrayList<>();
    }

    public Student(int userId, String uniqueId, String fullName, String email, String password,
                   String phone, String address, LocalDate dateOfBirth, String gender,
                   String rollNumber, int departmentId, LocalDate admissionDate,
                   int currentSemester, double cgpa) {
        super(userId, uniqueId, fullName, email, password, "student", phone, address, dateOfBirth, gender);
        this.rollNumber = rollNumber;
        this.departmentId = departmentId;
        this.admissionDate = admissionDate;
        this.currentSemester = currentSemester;
        this.cgpa = cgpa;
        this.totalCredits = 0;
        this.enrolledCourses = new ArrayList<>();
        this.enrollments = new ArrayList<>();
        this.attendances = new ArrayList<>();
        this.fees = new ArrayList<>();
    }

    // Getters and Setters
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

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

    public List<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; }

    public List<Attendance> getAttendances() { return attendances; }
    public void setAttendances(List<Attendance> attendances) { this.attendances = attendances; }

    public List<Fee> getFees() { return fees; }
    public void setFees(List<Fee> fees) { this.fees = fees; }

    public void addEnrollment(Enrollment enrollment) {
        this.enrollments.add(enrollment);
        this.enrolledCourses.add(enrollment.getCourse());
        this.totalCredits += enrollment.getCourse().getCredits();
        this.setStudentId(this.getId());
    }

    public double calculateGPA() {
        if (enrollments.isEmpty()) return 0.0;
        double totalGradePoints = 0;
        int totalCredits = 0;
        for (Enrollment enrollment : enrollments) {
            if (enrollment.isCompleted()) {
                totalGradePoints += enrollment.getGradePoints() * enrollment.getCourse().getCredits();
                totalCredits += enrollment.getCourse().getCredits();
            }
        }
        return totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;
    }

    public double getAttendancePercentage(int courseId) {
        if (attendances.isEmpty()) return 0.0;
        long present = attendances.stream()
                .filter(a -> a.getCourseId() == courseId &&
                        "Present".equals(a.getStatus()))
                .count();
        long total = attendances.stream()
                .filter(a -> a.getCourseId() == courseId)
                .count();
        return total > 0 ? (present * 100.0) / total : 0.0;
    }
}