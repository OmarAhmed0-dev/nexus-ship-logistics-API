package com.nexus.NexusShip.service;

import com.nexus.NexusShip.dto.request.AdminRegistrationRequest;
import com.nexus.NexusShip.dto.response.AdminResponse;
import com.nexus.NexusShip.dto.update.UserUpdateRequest;
import com.nexus.NexusShip.exception.UserAlreadyExists;
import com.nexus.NexusShip.exception.UserNotFound;
import com.nexus.NexusShip.mapper.AdminMapper;
import com.nexus.NexusShip.model.Admin;
import com.nexus.NexusShip.model.AdminRole;
import com.nexus.NexusShip.model.Driver;
import com.nexus.NexusShip.model.User;
import com.nexus.NexusShip.repository.AdminRepository;
import com.nexus.NexusShip.repository.DriverRepository;
import com.nexus.NexusShip.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final DriverRepository driverRepository;
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;


    private static final BigDecimal INITIAL_SALARY = BigDecimal.valueOf(7000);

    @Transactional
    public AdminResponse registerAdmin(AdminRegistrationRequest request) {
        //Check if the User is exists
        Optional<User> existingUser = userRepository.findUserByNationalIdEverywhere(request.nationalId());
        if (existingUser.isPresent()) {
            //Check if the user is admin
            User user = existingUser.get();

            //check if the user is a driver
            Optional<Driver> driver = driverRepository.findById(user.getId());
            if (driver.isPresent()) {
                throw new UserAlreadyExists("This user is an active driver. He must be deleted from drivers to become an Admin");
            }

            //Check if the user is admin
            Optional<Admin> existingAdmin = adminRepository.findById(user.getId());
            if (existingAdmin.isPresent()) {
                //The user is admin
                //Check if the admin is deleted or not
                Admin admin = existingAdmin.get();
                if (admin.isDeleted()) {
                    //The admin is deleted
                    admin.setDeleted(false);
                    return adminMapper.toResponse(adminRepository.save(admin));
                } else {
                    //The admin exists and not deleted
                    throw new UserAlreadyExists("This admin is already exist.");
                }
            } else {
                //The user is exists but not an admin
                return upgradeUserToAdmin(user);
            }

        }
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new UserAlreadyExists("Email is already in use.");
        }
        Admin admin = adminMapper.toEntity(request);

        admin.setPassword(passwordEncoder.encode(request.password()));
        admin.setSalary(INITIAL_SALARY);
        admin.setHireDate(LocalDateTime.now());

        return adminMapper.toResponse(adminRepository.save(admin));

    }

    @Transactional
    public AdminResponse upgradeUserToAdmin(User user) {
        adminRepository.insertAdminRole(user.getId());
        Admin admin = adminRepository.findById(user.getId())
                .orElseThrow(() -> new UserNotFound("Error during upgrading to admin"));
        admin.setHireDate(LocalDateTime.now());
        admin.setSalary(INITIAL_SALARY);
        return adminMapper.toResponse(adminRepository.save(admin));

    }


    public List<AdminResponse> findAllAdmins() {
        return adminRepository.findAll()
                .stream().map(adminMapper::toResponse).toList();
    }

    public AdminResponse findAdminById(Long id) {
        return adminRepository.findById(id).
                map(adminMapper::toResponse)
                .orElseThrow(() -> new UserNotFound("There is no admin with id " + id)
                );
    }

    @Transactional
    public void deleteAdmin(Long id) {
        Admin admin = adminRepository.findById(id).orElseThrow(() -> new UserNotFound("There is no admin with id " + id));
        admin.setDeleted(true);
        adminRepository.save(admin);
    }

    @Transactional
    public AdminResponse updateAdmin(Long id, UserUpdateRequest request) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new UserNotFound("There is no admin with id: " + id));
        adminMapper.updateEntityFromDto(request, admin);
        return adminMapper.toResponse(adminRepository.save(admin));
    }

    @Transactional
    public AdminResponse updateAdminRole(Long id, AdminRole newRole) {

        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new UserNotFound("There is no admin with id: " + id));

        admin.setAdminRole(newRole);
        return adminMapper.toResponse(adminRepository.save(admin));

    }

    @Transactional
    public AdminResponse raiseAdminSalary(Long id, BigDecimal raise) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new UserNotFound("There is no admin with id: " + id));

        admin.setSalary(admin.getSalary().add(raise));

        return adminMapper.toResponse(adminRepository.save(admin));

    }


}
