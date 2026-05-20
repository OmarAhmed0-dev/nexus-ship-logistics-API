package com.nexus.NexusShip.dto.response;

import com.nexus.NexusShip.model.VehicleStatus;
import com.nexus.NexusShip.model.VehicleType;

public record VehicleResponse (
        Long id,
        VehicleType vehicleType,
        String licensePlate,
        double maxWeight,
        double maxVolume,
        VehicleStatus status,
        double availableWeight,
        double availableVolume
){}
