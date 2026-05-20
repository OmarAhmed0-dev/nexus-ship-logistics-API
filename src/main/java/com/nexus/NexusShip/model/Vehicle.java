package com.nexus.NexusShip.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "vehicle")
@Getter
@Setter
@SQLRestriction("is_deleted = false")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @Column(name = "max_weight", nullable = false)
    private double maxWeight;

    @Column(name = "max_volume", nullable = false)
    private double maxVolume;

    @Column(name = "available_weight")
    private double availableWeight;

    @Column(name = "available_volume")
    private double availableVolume;

    @Column(name = "vehicle_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    @Column(name = "is_deleted")
    private boolean isDeleted;


    @Column(name = "license_plate")
    private String licensePlate;


    public Vehicle() {
    }

    public Vehicle(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }


    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", maxWeight=" + maxWeight +
                ", maxVolume=" + maxVolume +
                ", vehicleType=" + vehicleType +
                '}';
    }
}
