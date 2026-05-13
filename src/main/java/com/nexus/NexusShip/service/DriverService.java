package com.nexus.NexusShip.service;

import com.nexus.NexusShip.dto.request.DriverRegistrationRequest;
import com.nexus.NexusShip.dto.response.DriverResponse;
import com.nexus.NexusShip.dto.update.UserUpdateRequest;
import com.nexus.NexusShip.exception.UserAlreadyExists;
import com.nexus.NexusShip.exception.UserNotFound;
import com.nexus.NexusShip.mapper.DriverMapper;
import com.nexus.NexusShip.model.Admin;
import com.nexus.NexusShip.model.Driver;
import com.nexus.NexusShip.model.User;
import com.nexus.NexusShip.repository.AdminRepository;
import com.nexus.NexusShip.repository.DriverRepository;
import com.nexus.NexusShip.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class DriverService {
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final DriverMapper driverMapper;
    private final PasswordEncoder passwordEncoder;

    private static  final BigDecimal INITIAL_SALARY = BigDecimal.valueOf(5000);

    @Autowired
    public DriverService(DriverRepository driverRepository, UserRepository userRepository, AdminRepository adminRepository
            , DriverMapper driverMapper, PasswordEncoder passwordEncoder) {
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.driverMapper = driverMapper;
        this.passwordEncoder = passwordEncoder;

    }


    @Transactional
    public DriverResponse registerDriver(DriverRegistrationRequest request) {

        //First Check if the user is exits or as  active/deleted driver
        Optional<User> existingUser = userRepository.findUserByNationalIdEverywhere(request.nationalId());

        Optional<Driver> existingDriver = driverRepository.findByLicenseNumberEverywhere(request.licenseNumber());

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            //Check if the user is an active admin
            Optional<Admin> admin = adminRepository.findById(user.getId());
            if(admin.isPresent()) {
                throw new UserAlreadyExists("This user is an active admin. He must be deleted from admins to become a driver.");
            }



            //check if the user is a driver

            if (existingDriver.isPresent()) {
                //it means that the user is a driver
                //check if he is active or deleted driver
                Driver driver = existingDriver.get();
                if(!driver.getId().equals(user.getId())){
                    throw new UserAlreadyExists("This license number belongs to another driver.");
                }

                if (driver.isDeleted()) {
                    //Restore Old driver
                    driver.setDeleted(false);
                    return driverMapper.toResponse(driverRepository.save(driver));
                } else {
                    //Already active driver
                    throw new UserAlreadyExists("You are already exist as an active driver");
                }

            } else {
                //Upgrade sender to driver
                return upgradeSenderToDriver(user, request);
            }
        }
        if(existingDriver.isPresent()) {
            throw  new UserAlreadyExists("This license number already registered");
        }
        Driver newDriver = driverMapper.toEntity(request);
        newDriver.setPassword(passwordEncoder.encode(request.password()));
        newDriver.setRating(0.0);
        newDriver.setSalary(INITIAL_SALARY);
        return driverMapper.toResponse(driverRepository.save(newDriver));

    }

    @Transactional
    public DriverResponse upgradeSenderToDriver(User user, DriverRegistrationRequest request) {
        driverRepository.insertDriverRole(user.getId() , request.licenseNumber() ,0.0,INITIAL_SALARY);
        Driver upgradedDriver = driverRepository.findById(user.getId())
                .orElseThrow(()->new UserNotFound("Error during upgrade process."));
        return driverMapper.toResponse(upgradedDriver);
    }


    public List<DriverResponse> findAllDrivers() {
        return driverRepository.findAll().stream().map(driverMapper::toResponse).toList();
    }

    public DriverResponse findDriverById(Long id) {
        return driverRepository.findById(id)
                .map(driverMapper::toResponse)
                .orElseThrow(() -> new UserNotFound("There is no driver with id: " + id));
    }

    @Transactional
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id).orElseThrow(() -> new UserNotFound("There is no driver with id: " + id));
        driver.setDeleted(true);
        driverRepository.save(driver);
    }

    @Transactional
    public DriverResponse updateDriver(Long id, UserUpdateRequest request) {

        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new UserNotFound("Driver not found with id: " + id));

        if (request.firstName() != null && !request.firstName().isBlank()) {
            driver.setFirstName(request.firstName());
        }
        if (request.lastName() != null && !request.lastName().isBlank()) {
            driver.setLastName(request.lastName());
        }

        if (request.phoneNumber() != null) {
            userRepository.findByPhoneNumber(request.phoneNumber())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(id)) {
                            throw new UserAlreadyExists("This phone number is already user by another user");
                        }
                    });
            driver.setPhoneNumber(request.phoneNumber());
        }

        if (request.password() != null && !request.password().isBlank()) {
            driver.setPassword(passwordEncoder.encode(request.password()));
        }
        if (request.gender() != null) {
            driver.setGender(request.gender());
        }

        Driver updatedDriver = driverRepository.save(driver);
        return driverMapper.toResponse(updatedDriver);

    }

    public DriverResponse findDriverByLicenseNumber(String licenseNumber) {
        Optional<Driver> driver = driverRepository.findByLicenseNumber(licenseNumber);
        if (driver.isEmpty()) {
            throw new UserNotFound("There is no driver with License Number :" + licenseNumber);
        }
        return driverMapper.toResponse(driver.get());

    }

    public List<DriverResponse> findAvailableDriver() {
        return driverRepository.findAvailableDriver()
                .stream().map(driverMapper::toResponse).toList();
    }

    public List<DriverResponse> findTopRatedDriver(double minRating) {
        return driverRepository.findTopRatedDriver(minRating)
                .stream().map(driverMapper::toResponse).toList();
    }

    public List<DriverResponse> findAvailableHighRatedDriver(double minRating) {
        return driverRepository.findAvailableHighRatedDriver(minRating)
                .stream().map(driverMapper::toResponse).toList();
    }

    @Transactional
    public DriverResponse raiseSalary(Long id ,BigDecimal raiseAmount) {
       Driver driver = driverRepository.findById(id)
               .orElseThrow(() -> new UserNotFound("There is no Driver with id " +id));

       BigDecimal newSalary = driver.getSalary().add(raiseAmount);
       driver.setSalary(newSalary);
        return driverMapper.toResponse(driverRepository.save(driver));
    }


}
