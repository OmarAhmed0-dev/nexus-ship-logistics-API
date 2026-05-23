package com.nexus.NexusShip.service;

import com.nexus.NexusShip.dto.request.ShipmentRequest;
import com.nexus.NexusShip.dto.response.ShipmentHistoryResponse;
import com.nexus.NexusShip.dto.response.ShipmentResponse;
import com.nexus.NexusShip.dto.update.ShipmentUpdateRequest;
import com.nexus.NexusShip.exception.NotFoundException;
import com.nexus.NexusShip.mapper.ShipmentHistoryMapper;
import com.nexus.NexusShip.mapper.ShipmentMapper;
import com.nexus.NexusShip.model.*;
import com.nexus.NexusShip.repository.AdminRepository;
import com.nexus.NexusShip.repository.ReceiverRepository;
import com.nexus.NexusShip.repository.SenderRepository;
import com.nexus.NexusShip.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final ShipmentMapper shipmentMapper;
    private final SenderRepository senderRepository;
    private final ReceiverRepository receiverRepository;
    private final AdminRepository adminRepository;
    private final PricingService pricingService;
    private final ShipmentHistoryMapper shipmentHistoryMapper;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);


    @Transactional
    public ShipmentResponse createShipment(ShipmentRequest request) {
        if (!isWithinEgypt(request.pickupLatitude(), request.pickupLongitude()) ||
                !isWithinEgypt(request.destinationLatitude(), request.destinationLongitude())) {
            throw new IllegalArgumentException("Both pickup and destination locations must be with in Egypt borders");
        }

        Sender sender = senderRepository.findById(request.senderId())
                .orElseThrow(() -> new NotFoundException("Sender with id " + request.senderId() + " not found"));

        Receiver receiver = receiverRepository.findByPhoneNumber(request.receiverPhoneNumber())
                .orElseGet(() -> {
                    Receiver newReceiver = new Receiver();
                    newReceiver.setPhoneNumber(request.receiverPhoneNumber());
                    return receiverRepository.save(newReceiver);
                });

        Point pickupPoint = geometryFactory.createPoint(new Coordinate(request.pickupLongitude(), request.pickupLatitude()));
        Point destinationPoint = geometryFactory.createPoint(new Coordinate(request.destinationLongitude(), request.destinationLatitude()));


        Shipment shipment = new Shipment();
        shipment.setSender(sender);
        shipment.setReceiver(receiver);
        shipment.setDescription(request.description());
        shipment.setWeight(request.weight());
        shipment.setVolume(request.volume());
        shipment.setShipmentInsurance(request.shipmentInsurance());
        shipment.setShipmentValue(request.shipmentValue());

        shipment.setPickupGovernorate(request.pickupGovernorate());
        shipment.setPickupCity(request.pickupCity());
        shipment.setPickupAddress(request.pickupAddress());
        shipment.setPickUpLocation(pickupPoint);

        shipment.setDestinationGovernorate(request.destinationGovernorate());
        shipment.setDestinationCity(request.destinationCity());
        shipment.setDestinationAddress(request.destinationAddress());
        shipment.setDestinationLocation(destinationPoint);

        BigDecimal cost = pricingService.calculateShipmentCost(shipment);
        shipment.setCost(cost);

        ShipmentHistory initialHistory = new ShipmentHistory();
        initialHistory.setShipment(shipment);
        initialHistory.setStatus(ShipmentStatus.PENDING);
        initialHistory.setWhoMakeTheChange(sender);
        initialHistory.setUpdatedAt(LocalDateTime.now());
        initialHistory.setNote("Shipment created successfully and it is in pending status");
        shipment.changeStatus(initialHistory);

        return shipmentMapper.toResponse(shipmentRepository.save(shipment));


    }

    private boolean isWithinEgypt(double latitude, double longitude) {
        return (latitude >= 22.0 && latitude <= 31.7) &&
                (longitude >= 24.7 && longitude <= 37.0);
    }

    @Transactional
    public ShipmentResponse cancelShipment(Long shipmentId, Long userId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new NotFoundException("Shipment with id " + shipmentId + " not found"));


        boolean isAdmin = adminRepository.existsById(userId);
        boolean isOwner = shipment.getSender().getId().equals(userId);

        if (!isAdmin && !isOwner) {
            throw new IllegalArgumentException("Unauthorized: Only the sender who created this shipment can cancel it");
        }
        if (shipment.getStatus() != ShipmentStatus.PENDING) {
            throw new IllegalStateException("Cannot cancel shipment. current status is " + shipment.getStatus());
        }

        //Make a new history
        ShipmentHistory cancelHistory = new ShipmentHistory();
        cancelHistory.setShipment(shipment);
        cancelHistory.setStatus(ShipmentStatus.CANCELLED);
        cancelHistory.setUpdatedAt(LocalDateTime.now());
        if (isAdmin) {
            Admin admin = adminRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("Admin with id " + userId + " not found"));
            cancelHistory.setNote("Shipment wac cancelled by the Admin ID: " + userId);
            cancelHistory.setWhoMakeTheChange(admin);
        } else {
            cancelHistory.setNote("Shipment was cancelled by the Sender. ID: " + userId);
            cancelHistory.setWhoMakeTheChange(shipment.getSender());
        }
        shipment.changeStatus(cancelHistory);
        return shipmentMapper.toResponse(shipmentRepository.save(shipment));
    }


    public List<ShipmentResponse> findAllShipments(Long userId) {
        boolean isAdmin = adminRepository.existsById(userId);
        if(!isAdmin) {
            throw new SecurityException("Unauthorized: You do not have permission to view the shipments.");
        }
        return shipmentRepository.findAll().stream().map(shipmentMapper::toResponse).toList();
    }

    public List<ShipmentResponse> findAllShipmentsByStatus(ShipmentStatus status) {
        return shipmentRepository.findAllByStatus(status).stream().map(shipmentMapper::toResponse).toList();
    }

    public List<ShipmentResponse> findAllUserShipments(Long userId) {
        return shipmentRepository.findAllUserShipments(userId).stream().map(shipmentMapper::toResponse).toList();
    }

    public List<ShipmentHistoryResponse> getShipmentHistory(Long shipmentId ,Long userId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new NotFoundException("Shipment with id " + shipmentId + " not found"));
        boolean isAdmin = adminRepository.existsById(userId);
        boolean isOwner = shipment.getSender().getId().equals(userId);

        if (!isAdmin && !isOwner) {
            throw new IllegalArgumentException("Unauthorized: You do not have permission to view this shipment.");
        }

        return shipment.getHistory().stream()
                .map(shipmentHistoryMapper::toResponse).toList();
    }

    public ShipmentResponse findShipmentById(Long shipmentId, Long userId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new NotFoundException("Shipment with id " + shipmentId + " not found"));
        boolean isAdmin = adminRepository.existsById(userId);
        boolean isOwner = shipment.getSender().getId().equals(userId);

        if (!isAdmin && !isOwner) {
            throw new IllegalArgumentException("Unauthorized: You do not have permission to view this shipment.");
        }
        return shipmentMapper.toResponse(shipment);
    }

    @Transactional
    public ShipmentResponse updateShipmentDetails(Long shipmentId, Long userId, ShipmentUpdateRequest request) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new NotFoundException("Shipment not found"));

        if (!shipment.getSender().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized: Only the owner can update this shipment.");
        }

        if (shipment.getStatus() != ShipmentStatus.PENDING) {
            throw new IllegalStateException("Cannot update shipment details. Status is " + shipment.getStatus());
        }
        if (request.description() != null) {
            shipment.setDescription(request.description());
        }
        if (request.weight() != null) {
            shipment.setWeight(request.weight());
        }
        if (request.volume() != null) {
            shipment.setVolume(request.volume());
        }
        if (request.shipmentValue() != null) {
            shipment.setShipmentValue(request.shipmentValue());
        }
        if (request.shipmentInsurance() != null) {
            shipment.setShipmentInsurance(request.shipmentInsurance());
        }

        BigDecimal cost = pricingService.calculateShipmentCost(shipment);
        shipment.setCost(cost);

        ShipmentHistory updateHistory = new ShipmentHistory();
        updateHistory.setShipment(shipment);
        updateHistory.setStatus(shipment.getStatus());
        updateHistory.setWhoMakeTheChange(shipment.getSender());
        updateHistory.setUpdatedAt(LocalDateTime.now());
        updateHistory.setNote("Shipment details were updated by the sender.");
        shipment.changeStatus(updateHistory);

        return shipmentMapper.toResponse(shipmentRepository.save(shipment));
    }

}
