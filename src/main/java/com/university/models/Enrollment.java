// src/main/java/com/university/models/Enrollment.java
package com.university.models;

import java.time.LocalDate;

public class Enrollment {
    private int id;
    private int studentId;
    private String studentName;
    private int courseId;
    private String courseName;
    private String courseCode;
    private LocalDate enrollmentDate;
    private String grade;
    private double gradePoints;
    private boolean isCompleted;

    public Enrollment() {
        this.isCompleted = false;
        this.gradePoints = 0.0;
    }

    public Enrollment(int id, int studentId, int courseId, LocalDate enrollmentDate,
                      String grade, double gradePoints, boolean isCompleted) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.enrollmentDate = enrollmentDate;
        this.grade = grade;
        this.gradePoints = gradePoints;
        this.isCompleted = isCompleted;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public double getGradePoints() { return gradePoints; }
    public void setGradePoints(double gradePoints) { this.gradePoints = gradePoints; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}