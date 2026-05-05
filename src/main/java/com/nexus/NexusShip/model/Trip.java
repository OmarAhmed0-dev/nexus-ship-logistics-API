package com.nexus.NexusShip.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trip")
@Getter
@Setter
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @ManyToOne(cascade = {CascadeType.DETACH , CascadeType.MERGE, CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne(cascade = {CascadeType.DETACH , CascadeType.MERGE, CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @OneToMany(mappedBy = "trip" ,
            cascade ={CascadeType.DETACH , CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Shipment> shipmentList;


    @Column(name = "status",nullable = false)
    @Enumerated(EnumType.STRING)
    private TripStatus status;


    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    public Trip(){}
    public Trip(Driver driver, Vehicle vehicle) {
        this.driver = driver;
        this.vehicle = vehicle;
    }


    public void addShipment(Shipment shipment) {
        if(shipmentList == null){
            shipmentList = new ArrayList<>();
        }
        shipmentList.add(shipment);
        shipment.setTrip(this);
    }


    @Override
    public String toString() {
        return "Trip{" +
                "Id=" + id +
                ", driver=" + driver +
                ", vehicle=" + vehicle +
                ", shipmentList=" + shipmentList +
                ", status=" + status +
                ", startedAt=" + startedAt +
                ", endedAt=" + endedAt +
                '}';
    }
}
