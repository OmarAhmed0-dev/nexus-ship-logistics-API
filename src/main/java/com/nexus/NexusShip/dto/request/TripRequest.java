package com.nexus.NexusShip.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TripRequest(
        @NotBlank(message = "Governorate is required")
        String governorate,


        String city,

        @NotNull(message = "Driver ID is required")
        Long driverId,

        @NotNull(message = "Vehicle ID is required")
        Long vehicleId
) {}