package com.nexus.NexusShip.controller;

import com.nexus.NexusShip.dto.request.ShipmentRequest;
import com.nexus.NexusShip.dto.response.ShipmentHistoryResponse;
import com.nexus.NexusShip.dto.response.ShipmentResponse;
import com.nexus.NexusShip.dto.update.ShipmentUpdateRequest;
import com.nexus.NexusShip.model.ShipmentStatus;
import com.nexus.NexusShip.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {
    private final ShipmentService shipmentService;

    @PostMapping("/create")
    public ResponseEntity<ShipmentResponse> createShipment(@Valid @RequestBody ShipmentRequest shipmentRequest) {
        return new ResponseEntity<>(shipmentService.createShipment(shipmentRequest), HttpStatus.CREATED);
    }
  /*  @GetMapping
    public ResponseEntity<List<ShipmentResponse>> getAllShipments() {
        return ResponseEntity.ok(shipmentService.findAllShipments());
    }*/
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ShipmentResponse> cancelShipment(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(shipmentService.cancelShipment(id, userId));
    }
    @PutMapping("/{id}/update")
    public ResponseEntity<ShipmentResponse> updateShipment(
            @PathVariable Long id,
            @RequestParam Long userId,
            @Valid @RequestBody ShipmentUpdateRequest request) {
        return ResponseEntity.ok(shipmentService.updateShipmentDetails(id, userId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponse> getShipmentById(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(shipmentService.findShipmentById(id,userId));
    }
    @GetMapping("/user/{id}")
    public ResponseEntity<List<ShipmentResponse>> findAllUserShipments(@PathVariable Long id){
        return ResponseEntity.ok(shipmentService.findAllUserShipments(id));
    }
    @GetMapping("/{id}/history")
    public ResponseEntity<List<ShipmentHistoryResponse>> getAllShipmentHistory(
            @PathVariable Long id,
            @RequestParam Long userId
    ){
        return ResponseEntity.ok(shipmentService.getShipmentHistory(id, userId));
    }
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ShipmentResponse>> getAllShipmentsByStatus(@PathVariable ShipmentStatus status) {
        return ResponseEntity.ok(shipmentService.findAllShipmentsByStatus(status));
    }
    @GetMapping("/{userId}/admin")
    public ResponseEntity<List<ShipmentResponse>> getAllShipments(@PathVariable Long userId){
        return ResponseEntity.ok(shipmentService.findAllShipments(userId));
    }



}
