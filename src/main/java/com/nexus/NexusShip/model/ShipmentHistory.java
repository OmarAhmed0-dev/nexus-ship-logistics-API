package com.nexus.NexusShip.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name= "shipment_history")
@Getter
@Setter
public class ShipmentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    //many to one
    //many shipmentHistory record belongs to one shipment
    @ManyToOne(cascade = {CascadeType.DETACH , CascadeType.MERGE, CascadeType.PERSIST ,CascadeType.REFRESH} )
    @JoinColumn(name="shipment_id",nullable = false)
    private Shipment shipment;

    @Column(name = "status")
    private ShipmentStatus status;

    @ManyToOne(cascade = {CascadeType.DETACH , CascadeType.MERGE, CascadeType.PERSIST ,CascadeType.REFRESH} )
    @JoinColumn(name="who_make_the_change",nullable = false)
    private User whoMakeTheChange;

    @CreationTimestamp
    @Column(name = "updated_at",nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "note")
    private String note;

    public ShipmentHistory(){}

    public ShipmentHistory( User whoMakeTheChange , ShipmentStatus status, String note) {
        this.note = note;
        this.updatedAt = LocalDateTime.now();
        this.whoMakeTheChange = whoMakeTheChange;
        this.status = status;

    }

    @Override
    public String toString() {
        return "ShipmentHistory{" +
                "id=" + id +
                ", shipment=" + shipment +
                ", status=" + status +
                ", whoMakeTheChange=" + whoMakeTheChange +
                ", updatedAt=" + updatedAt +
                ", note='" + note + '\'' +
                '}';
    }

}
