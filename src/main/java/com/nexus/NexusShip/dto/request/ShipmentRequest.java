package com.nexus.NexusShip.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;

public record ShipmentRequest(

        @NotNull(message = "Sender ID required")
        Long senderId,

        @NotNull(message = "Weight is required")
        double weight,

        @NotNull(message = "Volume is required")
        double volume,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Shipment value is required")
        BigDecimal shipmentValue,

        @NotBlank(message = "Pickup governorate is required")
        String pickupGovernorate,

        @NotBlank(message = "Pickup city/district is required")
        String pickupCity,

        @NotBlank(message = "Detailed pickup address is required")
        @Size(min = 20, message = "Please provide a more detailed pickup address")
        String pickupAddress,

        @NotNull(message = "Pickup latitude is required")
        double pickupLatitude,

        @NotNull(message = "Pickup longitude is required")
        double pickupLongitude,

        @NotBlank(message = "Destination governorate is required")
        String destinationGovernorate,

        @NotBlank(message = "Destination city/district is required")
        String  destinationCity,

        @NotBlank(message = "Detailed destination address is required")
        @Size(min = 20, message = "Please provide a more detailed destination address")
        String  destinationAddress,

        @NotNull(message = "Destination latitude is required")
        double destinationLatitude,

        @NotNull(message = "Destination longitude is required")
        double destinationLongitude,



        // Creating a receiver Object if not Exist
        // if the receiver exist based on the phone number we will assign th shipment to him
        @NotBlank(message = "Receiver phone number is required")
        @Pattern(
                regexp = "^(0020|\\+20|0)?1[0125][0-9]{8}$",
                message = "Please enter a valid Egyptian mobile number"
        )
        String receiverPhoneNumber,

        boolean shipmentInsurance
) {}
