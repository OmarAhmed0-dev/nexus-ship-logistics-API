package com.nexus.NexusShip.mapper;

import com.nexus.NexusShip.dto.request.VehicleRegistrationRequest;
import com.nexus.NexusShip.dto.response.VehicleResponse;
import com.nexus.NexusShip.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(componentModel = "spring" , nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VehicleMapper {

    Vehicle toEntity(VehicleRegistrationRequest request);

    VehicleResponse toResponse(Vehicle vehicle);


   /* public Vehicle toEntity(VehicleRegistrationRequest request) {
        Vehicle vehicle = new Vehicle();

        vehicle.setVehicleType(request.vehicleType());
        vehicle.setLicensePlate(request.licensePlate());
        vehicle.setMaxWeight(request.vehicleType().getMaxWeight());
        vehicle.setMaxVolume(request.vehicleType().getMaxVolume());

        return vehicle;

    }

    public VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getVehicleType(),
                vehicle.getLicensePlate(),
                vehicle.getMaxWeight(),
                vehicle.getMaxVolume()
        );
    }*/
}
