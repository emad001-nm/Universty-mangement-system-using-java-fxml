// src/main/java/com/university/models/Enrollment.java
package com.university.models;

import java.time.LocalDate;

public class Enrollment {
    private int id;
    private Student student;
    private Course course;
    private LocalDate enrollmentDate;
    private String grade;
    private double gradePoints;
    private boolean isCompleted;

    public Enrollment() {}

    public Enrollment(int id, Student student, Course course, LocalDate enrollmentDate,
                      String grade, double gradePoints, boolean isCompleted) {
        this.id = id;
        this.student = student;
        this.course = course;
        this.enrollmentDate = enrollmentDate;
        this.grade = grade;
        this.gradePoints = gradePoints;
        this.isCompleted = isCompleted;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public double getGradePoints() { return gradePoints; }
    public void setGradePoints(double gradePoints) { this.gradePoints = gradePoints; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}