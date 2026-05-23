package com.nexus.NexusShip.repository;

import com.nexus.NexusShip.model.Receiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReceiverRepository extends JpaRepository<Receiver, Long> {

    @Query("select r from Receiver r where r.phoneNumber=:phoneNumber")
   Optional<Receiver> findByPhoneNumber(@Param("phoneNumber")String phoneNumber);
}
