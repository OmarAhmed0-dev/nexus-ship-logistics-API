package com.nexus.NexusShip.mapper;

import com.nexus.NexusShip.dto.response.ShipmentResponse;
import com.nexus.NexusShip.dto.response.TripResponse;
import com.nexus.NexusShip.model.Trip;
import org.springframework.stereotype.Component;

import java.util.List;



@Component
public class TripMapper {

    private final ShipmentMapper mapper;

    public TripMapper(ShipmentMapper mapper) {
        this.mapper = mapper;
    }
    

    public TripResponse toResponse(Trip trip){


        List<ShipmentResponse> shipmentResponseList = trip.getShipmentList()== null? List.of():
                trip.getShipmentList().stream().map(mapper::toResponse).toList();
        return new TripResponse(
                trip.getId(),
                trip.getDriver().getId(),
                trip.getVehicle().getId(),
                trip.getStartedAt(),
                trip.getEndedAt(),
                trip.getStatus(),
                shipmentResponseList


        );

    }
}
