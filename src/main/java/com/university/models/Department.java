// src/main/java/com/university/models/Department.java
package com.university.models;

import java.util.ArrayList;
import java.util.List;

public class Department {
    private int id;
    private String deptCode;
    private String deptName;
    private String faculty;
    private Teacher hod;
    private int establishedYear;
    private List<Course> courses;
    private List<Teacher> teachers;
    private List<Student> students;

    public Department() {
        this.courses = new ArrayList<>();
        this.teachers = new ArrayList<>();
        this.students = new ArrayList<>();
    }

    public Department(int id, String deptCode, String deptName, String faculty,
                      Teacher hod, int establishedYear) {
        this.id = id;
        this.deptCode = deptCode;
        this.deptName = deptName;
        this.faculty = faculty;
        this.hod = hod;
        this.establishedYear = establishedYear;
        this.courses = new ArrayList<>();
        this.teachers = new ArrayList<>();
        this.students = new ArrayList<>();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDeptCode() { return deptCode; }
    public void setDeptCode(String deptCode) { this.deptCode = deptCode; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public String getFaculty() { return faculty; }
    public void setFaculty(String faculty) { this.faculty = faculty; }

    public Teacher getHod() { return hod; }
    public void setHod(Teacher hod) { this.hod = hod; }

    public int getEstablishedYear() { return establishedYear; }
    public void setEstablishedYear(int establishedYear) { this.establishedYear = establishedYear; }

    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }

    public List<Teacher> getTeachers() { return teachers; }
    public void setTeachers(List<Teacher> teachers) { this.teachers = teachers; }

    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }

    public void addCourse(Course course) {
        this.courses.add(course);
    }

    public void addTeacher(Teacher teacher) {
        this.teachers.add(teacher);
    }

    public void addStudent(Student student) {
        this.students.add(student);
    }
}