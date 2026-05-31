package com.nexus.NexusShip.service;

import com.nexus.NexusShip.dto.request.TripRequest;
import com.nexus.NexusShip.dto.response.TripResponse;
import com.nexus.NexusShip.exception.NotFoundException;
import com.nexus.NexusShip.mapper.TripMapper;
import com.nexus.NexusShip.model.*;
import com.nexus.NexusShip.repository.DriverRepository;
import com.nexus.NexusShip.repository.ShipmentRepository;
import com.nexus.NexusShip.repository.TripRepository;
import com.nexus.NexusShip.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository tripRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final ShipmentRepository shipmentRepository;
    private final TripMapper tripMapper;

    @Transactional
    public TripResponse creatTrip(TripRequest request, ShipmentStatus shipmentStatus) {

        if ((shipmentStatus != ShipmentStatus.PENDING) && (shipmentStatus != ShipmentStatus.ARRIVED_AT_HUB)) {
            throw new IllegalStateException("Invalid Status");
        }

        tripRepository.findActiveTripForDriver(request.driverId()).ifPresent(trip -> {
            throw new IllegalStateException("Driver is already assigned to an ACTIVE trip.");
        });
        List<Shipment> shipmentList;
        if (shipmentStatus == ShipmentStatus.PENDING) {

            shipmentList = shipmentRepository
                    .findAllByStatusAndPickupGovernorateAndPickupCity(
                            ShipmentStatus.PENDING,
                            request.governorate(),
                            request.city()
                    );
        } else {
            shipmentList = shipmentRepository
                    .findAllByStatusAndDestinationGovernorateAndDestinationCity(
                            ShipmentStatus.ARRIVED_AT_HUB,
                            request.governorate(),
                            request.city());
        }


        if (shipmentList.isEmpty()) {
            throw new IllegalStateException("No target shipments found for " + request.governorate() + ", " + request.city());
        }
        Driver driver = driverRepository.findById(request.driverId())
                .orElseThrow(() -> new NotFoundException("Driver not found with id " + request.driverId()));

        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new NotFoundException("Vehicle not found with id " + request.vehicleId()));

        if (!vehicle.getStatus().equals(VehicleStatus.AVAILABLE)) {
            throw new IllegalStateException("Vehicle status is not AVAILABLE");
        }
        Trip trip = new Trip();
        trip.setStatus(TripStatus.READY);
        trip.setDriver(driver);
        trip.setVehicle(vehicle);

        for (Shipment shipment : shipmentList) {
            if (vehicleHaveCapacity(shipment, vehicle)) {
                shipment.setStatus(ShipmentStatus.ASSIGNED);
                vehicle.setAvailableWeight(vehicle.getAvailableWeight() - shipment.getWeight());
                vehicle.setAvailableVolume(vehicle.getAvailableVolume() - shipment.getVolume());
                trip.addShipment(shipment);

            }
        }
        if (trip.getShipmentList() == null || trip.getShipmentList().isEmpty()) {
            throw new IllegalStateException("No shipments could fit into the vehicle's capacity.");
        }
        vehicleRepository.save(vehicle);
        return tripMapper.toResponse(tripRepository.save(trip));

    }

    public boolean vehicleHaveCapacity(Shipment shipment, Vehicle vehicle) {

        return !(shipment.getVolume() > vehicle.getAvailableVolume()) &&
                !(shipment.getWeight() > vehicle.getAvailableWeight());

    }

    @Transactional
    public TripResponse startTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with id " + tripId));

        if (trip.getStatus() != TripStatus.READY) {
            throw new IllegalStateException("Cannot start trip. Current status is " + trip.getStatus());
        }
        trip.setStatus(TripStatus.ACTIVE);
        trip.setStartedAt(LocalDateTime.now());
        List<Shipment> shipmentList = trip.getShipmentList();

        for (Shipment shipment : shipmentList) {
            shipment.setStatus(ShipmentStatus.OUT_FOR_DELIVERY);
        }
        Vehicle vehicle = trip.getVehicle();
        vehicle.setStatus(VehicleStatus.ON_TRIP);
        vehicleRepository.save(vehicle);
        return tripMapper.toResponse(tripRepository.save(trip));
    }

    @Transactional
    public TripResponse endPickUpTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with id " + tripId));

        if (trip.getStatus() != TripStatus.ACTIVE) {
            throw new IllegalStateException("Cannot end trip. Current status is " + trip.getStatus());
        }
        List<Shipment> shipmentList = trip.getShipmentList();
        for (Shipment shipment : shipmentList) {
            shipment.setStatus(ShipmentStatus.ARRIVED_AT_HUB);
        }
        trip.setStatus(TripStatus.COMPLETED);
        trip.setEndedAt(LocalDateTime.now());
        Vehicle vehicle = trip.getVehicle();
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicleRepository.save(vehicle);
        return tripMapper.toResponse(tripRepository.save(trip));
    }

    @Transactional
    public TripResponse endDeliveryTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with id " + tripId));

        if (trip.getStatus() != TripStatus.ACTIVE) {
            throw new IllegalStateException("Cannot end trip. Current status is " + trip.getStatus());
        }
        List<Shipment> shipmentList = trip.getShipmentList();
        for (Shipment shipment : shipmentList) {
            shipment.setStatus(ShipmentStatus.DELIVERED);
        }
        trip.setStatus(TripStatus.COMPLETED);
        trip.setEndedAt(LocalDateTime.now());
        Vehicle vehicle = trip.getVehicle();
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicleRepository.save(vehicle);
        return tripMapper.toResponse(tripRepository.save(trip));
    }

    @Transactional
    public TripResponse cancelTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new NotFoundException("Trip not found with id " + tripId));

        if (trip.getStatus() == TripStatus.READY) {
            trip.setStatus(TripStatus.CANCELLED);
            Vehicle vehicle = trip.getVehicle();
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            vehicleRepository.save(vehicle);
            List<Shipment> shipmentList = trip.getShipmentList();
            for (Shipment shipment : shipmentList) {
                shipment.setStatus(ShipmentStatus.PENDING);
                vehicle.setAvailableWeight(vehicle.getAvailableWeight() + shipment.getWeight());
                vehicle.setAvailableVolume(vehicle.getAvailableVolume() + shipment.getVolume());
            }
        } else {
            throw new IllegalStateException("Cannot cancel trip. Current status is " + trip.getStatus());
        }
        return tripMapper.toResponse(tripRepository.save(trip));

    }

    @Transactional
    public void returnShipmentToSender(Long tripId, Long shipmentId) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new NotFoundException("Trip not found with id " + tripId));

        Shipment shipment = shipmentRepository.findById(shipmentId).orElseThrow(() -> new NotFoundException("Shipment not found with id " + shipmentId));

        List<Shipment> shipmentList = trip.getShipmentList();
        if (!shipmentList.contains(shipment)) {
            throw new IllegalStateException("Shipment does not belongs to this trip.");

        }

        if (shipment.getStatus() == ShipmentStatus.OUT_FOR_DELIVERY || shipment.getStatus() == ShipmentStatus.ARRIVED_AT_HUB
                || shipment.getStatus() == ShipmentStatus.ASSIGNED) {
            shipment.setStatus(ShipmentStatus.RETURNED_TO_SENDER);
        }
        if (shipment.getStatus() != ShipmentStatus.OUT_FOR_DELIVERY) {
            Vehicle vehicle = trip.getVehicle();
            vehicle.setAvailableWeight(vehicle.getAvailableWeight() + shipment.getWeight());
            vehicle.setAvailableVolume(vehicle.getAvailableVolume() + shipment.getVolume());
            vehicleRepository.save(vehicle);
        }
        shipmentRepository.save(shipment);
    }


}
