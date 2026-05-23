package com.nexus.NexusShip.dto.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateShipmentRequest(

        double weight,
        double volume,
        String description,
        BigDecimal shipmentValue,

        @Pattern(
                regexp = "^(0020|\\+20|0)?1[0125][0-9]{8}$",
                message = "Please enter a valid Egyptian mobile number"
        )
        String receiverPhoneNumber,
        boolean shipmentInsurance
) {
}
