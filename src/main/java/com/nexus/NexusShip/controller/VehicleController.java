package com.nexus.NexusShip.controller;

import com.nexus.NexusShip.dto.request.VehicleRegistrationRequest;
import com.nexus.NexusShip.dto.response.VehicleResponse;
import com.nexus.NexusShip.model.VehicleStatus;
import com.nexus.NexusShip.model.VehicleType;
import com.nexus.NexusShip.repository.VehicleRepository;
import com.nexus.NexusShip.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getVehicles(
            @RequestParam(required = false)VehicleStatus status,
            @RequestParam(required = false)VehicleType type
            ){
        return ResponseEntity.ok(vehicleService.searchVehicles(status, type));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long id){
        return ResponseEntity.ok(vehicleService.findVehicleById(id));
    }

    @PostMapping("/register")
    public ResponseEntity<VehicleResponse> registerVehicle(@RequestBody @Valid VehicleRegistrationRequest request){
        return new ResponseEntity<>(vehicleService.registerVehicle(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id){
        vehicleService.deleteVehicleById(id);
        return ResponseEntity.noContent().build();
    }

}
