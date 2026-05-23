package com.nexus.NexusShip.service;

import com.nexus.NexusShip.dto.request.ShipmentRequest;
import com.nexus.NexusShip.model.Shipment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PricingService {
    private static final BigDecimal BASE_FARE = BigDecimal.valueOf(20.0);
    private static final BigDecimal PER_KM_RATE = BigDecimal.valueOf(5.0);
    private static final BigDecimal PER_KG_RATE = BigDecimal.valueOf(2.0);
    private static final BigDecimal PER_M3_RATE = BigDecimal.valueOf(15.0);

    private static final int R = 6371;

    public BigDecimal calculateShipmentCost(Shipment shipment) {

        double pickupLat = shipment.getPickUpLocation().getY();
        double pickupLon = shipment.getPickUpLocation().getX();
        double destLat = shipment.getDestinationLocation().getY();
        double destLon = shipment.getDestinationLocation().getX();
        double distance = calculateDistanceInKm(pickupLat, pickupLon, destLat,destLon
        );
        BigDecimal distanceCost = BigDecimal.valueOf(distance).multiply(PER_KM_RATE);
        BigDecimal weightCost = BigDecimal.valueOf(shipment.getWeight()).multiply(PER_KG_RATE);
        BigDecimal volumeCost = BigDecimal.valueOf(shipment.getVolume()).multiply(PER_M3_RATE);

        BigDecimal totalCost = BASE_FARE.add(distanceCost).add(weightCost).add(volumeCost);


        if (shipment.isShipmentInsurance()) {
            BigDecimal insuranceFee = shipment.getShipmentValue().multiply(BigDecimal.valueOf(0.01));
            totalCost = totalCost.add(insuranceFee);
        }

        return totalCost;
    }

    private double calculateDistanceInKm(double lat1, double lon1, double lat2, double lon2) {

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) +
                Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;

    }
}
