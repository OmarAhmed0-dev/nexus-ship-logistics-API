package com.nexus.NexusShip.model;

import jakarta.persistence.*;

//java.awt.Point is for desktop GUI coordinates (pixels on a screen). It will not work with PostGIS or spatial database queries.
//So we use
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipment")
@Getter
@Setter
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
    @ManyToOne(cascade ={CascadeType.DETACH , CascadeType.MERGE,
                         CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "sender_id",nullable = false)
    private Sender sender;

    @ManyToOne(cascade = {CascadeType.DETACH , CascadeType.MERGE,
                          CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "receiver_id",nullable = false)
    private Receiver receiver;


    @ManyToOne( cascade ={CascadeType.DETACH , CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH})
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

    @Column(name = "shipment_insurance" , columnDefinition = "false")
    private boolean shipmentInsurance;

    @Column(name = "shipment_cost")
    private BigDecimal cost;

    @Column(columnDefinition = "geometry(Point, 4326)" , name = "pickup_location" ,nullable = false)
    private Point pickUpLocation;

    @Column(columnDefinition = "geometry(Point, 4326)" , name = "destination_location",nullable = false)
    private Point destinationLocation;


    @OneToMany(mappedBy = "shipment" ,
            cascade = {CascadeType.DETACH , CascadeType.MERGE, CascadeType.PERSIST ,CascadeType.REFRESH})
    private List<ShipmentHistory> history;

    public void changeStatus(ShipmentHistory action) {
        if(history == null) {
            history = new ArrayList<>();
        }
        this.setStatus(action.getStatus());
        history.add(action);
        action.setShipment(this);
    }



    public Shipment(){}

    public Shipment(LocalDateTime createdAt, LocalDateTime arrivedAt, Receiver receiver, Sender sender, double weight,
                    double volume, ShipmentStatus status, BigDecimal shipmentValue, boolean shippingInsurace, BigDecimal cost,
                    Point pickUpLocation, Point destinationLocation , String description) {
        this.createdAt = createdAt;
        this.arrivedAt = arrivedAt;
        this.receiver = receiver;
        this.sender = sender;
        this.weight = weight;
        this.volume = volume;
        this.status = status;
        this.shipmentValue = shipmentValue;
        this.shipmentInsurance = shippingInsurace;
        this.cost = cost;
        this.pickUpLocation = pickUpLocation;
        this.destinationLocation = destinationLocation;
        this.description = description;
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
                ", shippingInsurace=" + shipmentInsurance +
                ", cost=" + cost +
                ", pickUpLocation=" + pickUpLocation +
                ", destinationLocation=" + destinationLocation +
                ", history=" + history +
                '}';
    }
}
