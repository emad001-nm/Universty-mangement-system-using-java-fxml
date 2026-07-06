// src/main/java/com/university/models/Course.java
package com.university.models;

public class Course {
    private int id;
    private String courseCode;
    private String courseName;
    private int credits;
    private String department;
    private Teacher teacher;
    private int semester;
    private String courseType;
    private int maxStudents;
    private int enrolledCount;

    public Course() {
        this.maxStudents = 50;
        this.enrolledCount = 0;
    }

    public Course(int id, String courseCode, String courseName, int credits,
                  String department, Teacher teacher, int semester,
                  String courseType, int maxStudents) {
        this.id = id;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.department = department;
        this.teacher = teacher;
        this.semester = semester;
        this.courseType = courseType;
        this.maxStudents = maxStudents;
        this.enrolledCount = 0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public String getCourseType() { return courseType; }
    public void setCourseType(String courseType) { this.courseType = courseType; }

    public int getMaxStudents() { return maxStudents; }
    public void setMaxStudents(int maxStudents) { this.maxStudents = maxStudents; }

    public int getEnrolledCount() { return enrolledCount; }
    public void setEnrolledCount(int enrolledCount) { this.enrolledCount = enrolledCount; }

    public boolean isAvailable() {
        return enrolledCount < maxStudents;
    }
}