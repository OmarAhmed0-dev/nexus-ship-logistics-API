package com.nexus.NexusShip.mapper;

import com.nexus.NexusShip.dto.request.ShipmentRequest;
import com.nexus.NexusShip.dto.response.ShipmentResponse;
import com.nexus.NexusShip.model.Receiver;
import com.nexus.NexusShip.model.Sender;
import com.nexus.NexusShip.model.Shipment;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring"  ,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ShipmentMapper {

    public Shipment toEntity(ShipmentRequest request);

    public ShipmentResponse toResponse(Shipment shipment);

   /* public Shipment toEntity(ShipmentRequest request , Sender sender, Receiver receiver) {

        Shipment shipment = new Shipment();
        shipment.setWeight(request.weight());
        shipment.setVolume(request.volume());
        shipment.setDescription(request.description());
        shipment.setShipmentValue(request.shipmentValue());
        shipment.setShipmentInsurance(request.shipmentInsurance());
        shipment.setPickUpLocation(request.pickUpLocation());
        shipment.setDestinationLocation(request.destinationLocation());

        shipment.setSender(sender);
        shipment.setReceiver(receiver);

        return shipment;
    }

    public ShipmentResponse toResponse(Shipment shipment) {
        return new ShipmentResponse(
                shipment.getId(),
                shipment.getDescription(),
                shipment.getWeight(),
                shipment.getVolume(),
                shipment.getShipmentValue(),
                shipment.isShipmentInsurance(),
                shipment.getStatus(),
                shipment.getCost(),
                shipment.getCreatedAt(),
                shipment.getPickUpLocation(),
                shipment.getDestinationLocation()

        );

    }*/
}
