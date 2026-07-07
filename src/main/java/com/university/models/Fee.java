// src/main/java/com/university/models/Fee.java
package com.university.models;

import java.time.LocalDate;

public class Fee {
    private int id;
    private int studentId;
    private String studentName;
    private int semester;
    private double totalAmount;
    private double paidAmount;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private String status;

    public Fee() {}

    public Fee(int id, int studentId, int semester, double totalAmount,
               double paidAmount, LocalDate dueDate, LocalDate paymentDate, String status) {
        this.id = id;
        this.studentId = studentId;
        this.semester = semester;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.dueDate = dueDate;
        this.paymentDate = paymentDate;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }

    public double getDueAmount() {
        return totalAmount - paidAmount;
    }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}