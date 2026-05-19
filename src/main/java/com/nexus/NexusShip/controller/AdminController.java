package com.nexus.NexusShip.controller;

import com.nexus.NexusShip.dto.request.AdminRegistrationRequest;
import com.nexus.NexusShip.dto.response.AdminResponse;
import com.nexus.NexusShip.dto.update.UserUpdateRequest;
import com.nexus.NexusShip.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admins")
public class AdminController {

    private final AdminService adminService;

    @GetMapping()
    public ResponseEntity<List<AdminResponse>> getAllAdmins() {
        return ResponseEntity.ok(adminService.findAllAdmins());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminResponse> getAdminById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.findAdminById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdminById(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminResponse> updateAdminById(@PathVariable Long id, @RequestBody @Valid UserUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateAdmin(id, request));
    }

    @PostMapping("/register")
    public ResponseEntity<AdminResponse> registerAdmin(@RequestBody @Valid AdminRegistrationRequest request) {
        return new ResponseEntity<>(adminService.registerAdmin(request), HttpStatus.CREATED);
    }



}
