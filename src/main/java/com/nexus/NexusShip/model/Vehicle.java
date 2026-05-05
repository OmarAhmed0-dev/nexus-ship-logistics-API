package com.nexus.NexusShip.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "vehicle")
@Getter
@Setter
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @Column(name = "max_weight",nullable = false)
    private double maxWeight;

    @Column(name = "max_height",nullable = false)
    private double  maxHeight;

    @Column(name = "vehicle_type",nullable = false)
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    public Vehicle(){}

    public Vehicle(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }


    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", maxWeight=" + maxWeight +
                ", maxHeight=" + maxHeight +
                ", vehicleType=" + vehicleType +
                '}';
    }
}
