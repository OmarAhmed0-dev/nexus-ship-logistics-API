package com.nexus.NexusShip.model;

import jakarta.persistence.*;

//java.awt.Point is for desktop GUI coordinates (pixels on a screen). It will not work with PostGIS or spatial database queries.
//So we use
import org.locationtech.jts.geom.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name ="created_at",nullable = false)
    private LocalDateTime createdAt;

    @Column(name ="arrived_at")
    private LocalDateTime arrivedAt;

    //Many Shipments belong to one sender
    @ManyToOne(cascade ={CascadeType.DETACH , CascadeType.MERGE})
    @JoinColumn(name = "sender_id",nullable = false)
    private Sender sender;

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "receiver_id",nullable = false)
    private Receiver receiver;


    @ManyToOne()
    @JoinColumn(name = "trip_id")
    private Trip trip;



    @Column(name = "description",nullable = false)
    private String description;

    @Column(name = "weight",nullable = false)
    private double weight;

    @Column(name = "volume",nullable = false)
    private double volume;

    @Column(name = "status",nullable = false)
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    @Column(name = "shipment_value")
    private BigDecimal shipmentValue;

    @Column(name = "shipment_insurance")
    private boolean shipmentInsurance;

    @Column(name = "shipment_cost")
    private BigDecimal cost;

    @Column(name = "pickup_governorate",nullable = false)
    private String pickupGovernorate;

    @Column(name = "pickup_city",nullable = false)
    private String pickupCity;

    @Column(name = "pickup_address",nullable = false)
    private String pickupAddress;

    @Column(columnDefinition = "geometry(Point, 4326)" , name = "pickup_location" ,nullable = false)
    private Point pickUpLocation;

    @Column(name = "destination_governorate" ,nullable = false)
    private String destinationGovernorate;

    @Column(name = "destination_city",nullable = false)
    private String destinationCity;

    @Column(name = "destination_address",nullable = false)
    private String destinationAddress;
    
    @Column(columnDefinition = "geometry(Point, 4326)" , name = "destination_location",nullable = false)
    private Point destinationLocation;


    @OneToMany(mappedBy = "shipment" ,
            cascade = CascadeType.ALL , orphanRemoval = true)
    private List<ShipmentHistory> history;

    public void changeStatus(ShipmentHistory action) {
        if(history == null) {
            history = new ArrayList<>();
        }
        this.setStatus(action.getStatus());
        history.add(action);
        action.setShipment(this);
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


    @Override
    public String toString() {
        return "Shipment{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", arrivedAt=" + arrivedAt +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", trip=" + trip +
                ", description='" + description + '\'' +
                ", weight=" + weight +
                ", volume=" + volume +
                ", status=" + status +
                ", shipmentValue=" + shipmentValue +
                ", shipmentInsurance=" + shipmentInsurance +
                ", cost=" + cost +
                ", pickupGovernorate='" + pickupGovernorate + '\'' +
                ", pickupCity='" + pickupCity + '\'' +
                ", pickupAddress='" + pickupAddress + '\'' +
                ", destinationGovernorate='" + destinationGovernorate + '\'' +
                ", destinationCity='" + destinationCity + '\'' +
                ", destinationAddress='" + destinationAddress + '\'' +
                '}';
    }
}
