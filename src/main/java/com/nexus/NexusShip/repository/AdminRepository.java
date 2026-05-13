package com.nexus.NexusShip.repository;

import com.nexus.NexusShip.model.Admin;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Long> {

    @Modifying
    @Transactional
    @Query(value = "insert into admin_user (id) VALUES (:id)",nativeQuery = true)
    void insertAdminRole(@Param("id") Long id);
}
