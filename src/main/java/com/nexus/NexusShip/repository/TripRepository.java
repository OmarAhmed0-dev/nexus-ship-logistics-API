package com.nexus.NexusShip.repository;

import com.nexus.NexusShip.model.Trip;
import com.nexus.NexusShip.model.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    //find all trips with specific status
    @Query("select t from Trip t where t.status = :status order by  t.startedAt DESC ")
    List<Trip> findTripByStatus(@Param("status") TripStatus status);


    //Find Active trip for Specific driver

    @Query("SELECT t from Trip t where t.driver.id = :driverId AND( t.status='ACTIVE' OR t.status='READY')")
    Optional<Trip> findActiveTripForDriver(@Param("driverId") Long driverId);








}
