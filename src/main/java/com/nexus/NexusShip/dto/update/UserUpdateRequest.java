package com.nexus.NexusShip.dto.update;


import com.nexus.NexusShip.model.Gender;
import jakarta.validation.constraints.*;

public record UserUpdateRequest(
        String firstName,

        String lastName,

        @Size(min = 8 , message = "Password must be at least 8 characters")
        String password,

        @Pattern(
                regexp = "^(0020|\\+20|0)?1[0125][0-9]{8}$",
                message = "Please enter a valid Egyptian mobile number"
        )
        String phoneNumber,

        @NotNull(message = "Gender is required")
        Gender gender

)
{}
