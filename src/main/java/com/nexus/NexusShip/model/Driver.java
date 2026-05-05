package com.nexus.NexusShip.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "driver")
@Getter
@Setter
public class Driver extends User{

    @Column(name = "license_number",nullable = false)
    private String licenseNumber;

    @Column(name = "salary",nullable = false)
    private double salary;

    @Column(name = "rating")
    private double rating;



    @OneToMany(mappedBy = "driver",
            cascade = {CascadeType.DETACH , CascadeType.MERGE, CascadeType.PERSIST ,CascadeType.REFRESH})
    private List<Trip> tripList;


    public void addTrip(Trip trip) {
        if(tripList == null) {
            tripList = new ArrayList<>();
        }
        tripList.add(trip);
        trip.setDriver(this);

    }



    public Driver(){}

    public Driver(String firstName, String lastName, Gender gender, String nationalId, String email, String password,
                  String phoneNumber, String licenseNumber, double salary) {
        super(firstName, lastName, gender, nationalId, email, password, phoneNumber);

        this.licenseNumber = licenseNumber;
        this.salary = salary;
    }




    @Override
    public String toString() {
        return "Driver{" +
                "licenseNumber=" + licenseNumber +
                ", salary=" + salary +
                ", rating=" + rating +
                '}';
    }
}
