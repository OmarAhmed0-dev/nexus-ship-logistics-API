package com.nexus.NexusShip.controller;

import com.nexus.NexusShip.dto.request.TripRequest;
import com.nexus.NexusShip.dto.response.TripResponse;
import com.nexus.NexusShip.model.ShipmentStatus;
import com.nexus.NexusShip.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/trips")
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(
            @Valid @RequestBody TripRequest tripRequest,
            @RequestParam ShipmentStatus status) {

        return new ResponseEntity<>(tripService.createTrip(tripRequest, status), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<TripResponse> startTrip(@PathVariable Long id) {
        return ResponseEntity.ok(tripService.startTrip(id));
    }

    @PutMapping("/{id}/end-pickup")
    public ResponseEntity<TripResponse> endPickupTrip(@PathVariable Long id) {
        return ResponseEntity.ok(tripService.endPickUpTrip(id));
    }

    @PutMapping("/{id}/end-delivery")
    public ResponseEntity<TripResponse> endDeliveryTrip(@PathVariable Long id) {
        return ResponseEntity.ok(tripService.endDeliveryTrip(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<TripResponse> cancelTrip(@PathVariable Long id) {
        return ResponseEntity.ok(tripService.cancelTrip(id));
    }


    @PutMapping("/{tripId}/shipments/{shipmentId}/return")
    public ResponseEntity<Void> returnShipmentToSender(
            @PathVariable Long tripId,
            @PathVariable Long shipmentId
    ) {
        tripService.returnShipmentToSender(tripId, shipmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TripResponse>> getTrips() {
        return ResponseEntity.ok(tripService.findAllTrips());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> getTrip(@PathVariable Long id) {
        return ResponseEntity.ok(tripService.findTripById(id));
    }
}
