package com.nexus.NexusShip.service;

import com.nexus.NexusShip.dto.request.VehicleRegistrationRequest;
import com.nexus.NexusShip.dto.response.VehicleResponse;
import com.nexus.NexusShip.exception.AlreadyExistsException;
import com.nexus.NexusShip.exception.NotFoundException;
import com.nexus.NexusShip.mapper.VehicleMapper;
import com.nexus.NexusShip.model.Vehicle;
import com.nexus.NexusShip.model.VehicleStatus;
import com.nexus.NexusShip.model.VehicleType;
import com.nexus.NexusShip.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;


    public List<VehicleResponse> findAllVehicles() {
        return vehicleRepository.findAll().stream().map(vehicleMapper::toResponse).toList();
    }

    public VehicleResponse findVehicleById(Long vehicleId) {
        return vehicleMapper.toResponse(vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle with id " + vehicleId + " not found")));
    }

    public List<VehicleResponse> findAllVehiclesByType(VehicleType vehicleType) {
        return vehicleRepository.findAllVehiclesByType(vehicleType).stream().map(vehicleMapper::toResponse).toList();
    }

    public List<VehicleResponse> findAvailableVehiclesByType(VehicleType vehicleType) {
        return vehicleRepository.findAvailableVehiclesByType(vehicleType).stream().map(vehicleMapper::toResponse).toList();
    }

    @Transactional
    public void deleteVehicleById(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle with id " + vehicleId + " not found"));
        vehicle.setDeleted(true);
        vehicleRepository.save(vehicle);
    }

    @Transactional
    public VehicleResponse registerVehicle(VehicleRegistrationRequest request) {
        Optional<Long> vehicleId = vehicleRepository.findVehicleIdByLicensePlateIncludingDeleted(request.licensePlate());

        if (vehicleId.isPresent()) {
            Long existingVehicleId = vehicleId.get();

            Optional<Vehicle> existingVehicle = vehicleRepository.findVehicleByIdIncludingDeleted(existingVehicleId);
            if (existingVehicle.isPresent()) {
                Vehicle vehicle = existingVehicle.get();
                if (vehicle.isDeleted()) {
                    return RestoreVehicle(vehicle);
                } else {
                    throw new AlreadyExistsException("Vehicle with License Plate " + request.licensePlate() + " already exists");
                }
            }


        }

        Vehicle vehicle = vehicleMapper.toEntity(request);
        vehicle.setMaxWeight(request.vehicleType().getMaxWeight());
        vehicle.setMaxVolume(request.vehicleType().getMaxVolume());

        vehicle.setAvailableVolume(vehicle.getMaxVolume());
        vehicle.setAvailableWeight(vehicle.getMaxWeight());
        return vehicleMapper.toResponse(vehicleRepository.save(vehicle));


    }

    @Transactional
    public VehicleResponse RestoreVehicle(Vehicle vehicle) {
        vehicle.setDeleted(false);
        vehicle.setAvailableVolume(vehicle.getMaxVolume());
        vehicle.setAvailableWeight(vehicle.getMaxWeight());
        return vehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    public List<VehicleResponse> findAllAvailableVehicles(){
        return vehicleRepository.findAllAvailableVehicles().stream()
                .map(vehicleMapper::toResponse).toList();
    }

    public List<VehicleResponse> findAllInMaintenanceVehicles(){
        return vehicleRepository.findAllInMaintenanceVehicles().stream()
                .map(vehicleMapper::toResponse).toList();
    }

    public List<VehicleResponse> findAllOnTripVehicles(){
        return vehicleRepository.findAllOnTripVehicles().stream()
                .map(vehicleMapper::toResponse).toList();
    }


    //Search for vehicles to be used in the controller

    public List<VehicleResponse> searchVehicles (VehicleStatus status,VehicleType type){
        if(status == null && type == null) {
            return findAllVehicles();
        }
        if(status == null) {
            return findAllVehiclesByType(type);
        }
        if(type!=null) {
           if(status == VehicleStatus.AVAILABLE) {
               // Find available vehicles by type
               return findAvailableVehiclesByType(type);
           }
        }
        return switch (status) {
            case AVAILABLE -> findAllAvailableVehicles();
            case MAINTENANCE -> findAllInMaintenanceVehicles();
            case ON_TRIP -> findAllOnTripVehicles();
            case ASSIGNED -> findAllAssignedVehicles();
        };
    }

    private List<VehicleResponse> findAllAssignedVehicles() {
        return  vehicleRepository.findAllAssignedVehicles().stream().map(vehicleMapper::toResponse).toList();
    }


}
