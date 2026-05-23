package com.nexus.NexusShip.dto.response;

import com.nexus.NexusShip.model.ShipmentStatus;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ShipmentResponse(
        Long id,
        String description,
        double weight,
        double volume,
        BigDecimal shipmentValue,
        boolean shipmentInsurance,
        ShipmentStatus status,
        BigDecimal cost,
        LocalDateTime createdAt,
        String pickupGovernorate,
        String pickupCity,
        String pickupAddress,
        String destinationGovernorate,
        String destinationCity,
        String destinationAddress

) {}
