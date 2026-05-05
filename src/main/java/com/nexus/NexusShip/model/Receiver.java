package com.nexus.NexusShip.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "receiver")
@Getter
@Setter
public class Receiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "phone_number",nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "receiver" ,
               cascade ={CascadeType.DETACH , CascadeType.MERGE,
                         CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Shipment> shipmentList;

    public Receiver(){}

    public Receiver(String phoneNumber) {
        this.phoneNumber = phoneNumber;

    }


    public void addShipment(Shipment shipment){
        if(shipmentList == null){
            shipmentList = new ArrayList<>();
        }
        shipmentList.add(shipment);
        shipment.setReceiver(this);

    }



    @Override
    public String toString() {
        return "Receiver{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }


}
