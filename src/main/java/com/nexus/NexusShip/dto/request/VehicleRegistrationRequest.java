package com.nexus.NexusShip.dto.request;

import com.nexus.NexusShip.model.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record VehicleRegistrationRequest(

        @NotNull(message = "Vehicle type is required")
        VehicleType vehicleType,

        @NotBlank(message = "License plate cannot be blank")
        @Pattern(
                regexp = "^[\\u0621-\\u064A]\\s*[\\u0621-\\u064A]\\s*[\\u0621-\\u064A]\\s*[0-9]{3,4}$",
                message = "Invalid Egyptian license plate format. Must be 3 Arabic letters and 3 or 4 digits (spaces are allowed)."
        )
        String licensePlate





) {}
