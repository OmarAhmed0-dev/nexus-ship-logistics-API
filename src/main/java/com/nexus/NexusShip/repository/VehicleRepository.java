package com.nexus.NexusShip.repository;

import com.nexus.NexusShip.model.Vehicle;
import com.nexus.NexusShip.model.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.geolatte.geom.V;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT v from Vehicle v where v.vehicleType = :vehicleType ")
    List<Vehicle> findAllVehiclesByType(VehicleType vehicleType);


    @Query("SELECT v from Vehicle v where v.vehicleType = :vehicleType " +
            "and v.status ='AVAILABLE'")
    List<Vehicle> findAvailableVehiclesByType(@Param("vehicleType") VehicleType vehicleType);

    @Query("SELECT v from Vehicle v where v.status = 'AVAILABLE' ")
    List<Vehicle> findAllAvailableVehicles();

    @Query("SELECT v from Vehicle v where v.status = 'MAINTENANCE' ")
    List<Vehicle> findAllInMaintenanceVehicles();

    @Query("SELECT v from Vehicle v where v.status = 'ON_TRIP' ")
    List<Vehicle> findAllOnTripVehicles();

    @Query("SELECT v from Vehicle v where v.status = 'ASSIGNED' ")
    List<Vehicle> findAllAssignedVehicles();



    @Query(value = "select  id from vehicle where license_plate =:licensePlate" , nativeQuery = true)
    Optional<Long> findVehicleIdByLicensePlateIncludingDeleted(@Param("licensePlate") String licensePlate);

    @Query(value = "select * from vehicle where id =:id", nativeQuery = true)
    Optional<Vehicle> findVehicleByIdIncludingDeleted(@Param("id") Long id);


    @Query("select v from Vehicle v where v.licensePlate =:licensePlate")
    Optional<Vehicle> findVehicleIdByLicensePlate(String licensePlate);
}
