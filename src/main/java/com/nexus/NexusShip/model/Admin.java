package com.nexus.NexusShip.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_user")
@Getter
@Setter
public class Admin extends User{

    @Column(name = "salary" ,nullable = false)
    private BigDecimal salary;

    @Column(name = "hire_date",nullable = false)
    private LocalDateTime hireDate;

    @Column(name = "admin_role",nullable = false)
    private AdminRole adminRole;



    public Admin(){}

    public Admin(String firstName, String lastName, Gender gender, String nationalId, String email, String password, String phoneNumber,
                 BigDecimal salary, AdminRole adminRole, LocalDateTime hireDate) {
        super(firstName, lastName, gender, nationalId, email, password, phoneNumber);
        this.salary = salary;
        this.adminRole = adminRole;
        this.hireDate = hireDate;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "salary=" + salary +
                ", hireDate=" + hireDate +
                ", adminRole=" + adminRole +
                '}';
    }
}

