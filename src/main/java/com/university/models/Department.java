// src/main/java/com/university/models/Department.java
package com.university.models;

import java.util.ArrayList;
import java.util.List;

public class Department {
    private int id;
    private String deptCode;
    private String deptName;
    private String faculty;
    private int hodId;
    private String hodName;
    private int establishedYear;
    private List<Course> courses;

    public Department() {
        this.courses = new ArrayList<>();
    }

    public Department(int id, String deptCode, String deptName, String faculty,
                      int hodId, int establishedYear) {
        this.id = id;
        this.deptCode = deptCode;
        this.deptName = deptName;
        this.faculty = faculty;
        this.hodId = hodId;
        this.establishedYear = establishedYear;
        this.courses = new ArrayList<>();
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

    public int getHodId() { return hodId; }
    public void setHodId(int hodId) { this.hodId = hodId; }

    public String getHodName() { return hodName; }
    public void setHodName(String hodName) { this.hodName = hodName; }

    public int getEstablishedYear() { return establishedYear; }
    public void setEstablishedYear(int establishedYear) { this.establishedYear = establishedYear; }

    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }

    @Override
    public String toString() {
        return deptName + " (" + deptCode + ")";
    }
}