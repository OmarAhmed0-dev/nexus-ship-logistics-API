package com.nexus.NexusShip.repository;

import com.nexus.NexusShip.model.Shipment;
import com.nexus.NexusShip.model.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment,Long> {

    //Find shipments that ready to go and not assigned to trip

    @Query("select s from Shipment s where s.status='PENDING' and s.trip IS NULL")
    List<Shipment> findUnassignedShipments();

    //Find all shipments by status

    @Query("select s from Shipment s where s.status=:status")
    List<Shipment> findAllByStatus(@Param("status") ShipmentStatus status);

    @Query("select s from Shipment s where s.sender.id =:id")
    List<Shipment> findAllUserShipments(@Param("id")Long id);

    List<Shipment> findAllByStatusAndPickupGovernorateAndPickupCity (ShipmentStatus shipmentStatus, String pickupGovernorate, String pickupCity);
    List<Shipment> findAllByStatusAndDestinationGovernorateAndDestinationCity(ShipmentStatus shipmentStatus, String pickupGovernorate, String pickupCity);
}
