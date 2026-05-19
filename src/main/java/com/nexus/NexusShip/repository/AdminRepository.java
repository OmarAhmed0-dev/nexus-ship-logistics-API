package com.nexus.NexusShip.repository;

import com.nexus.NexusShip.model.Admin;
import com.nexus.NexusShip.model.Driver;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Long> {

    @Modifying
    @Transactional
    @Query(value = "insert into admin_user (admin_id) VALUES (:id)",nativeQuery = true)
    void insertAdminRole(@Param("id") Long id);


    @Query(value = "select u.* , a.*  from admin_user a join users u ON  a.admin_id = u.id WHERE a.admin_id = :id", nativeQuery = true)
    Optional<Admin> findByIdEverywhere(@Param("id") Long id);

    @Query(value = "select a.admin_id from admin_user a where a.admin_id=:id", nativeQuery = true)
    Optional<Long> findAdminIdByIdEverywhere(@Param("id") Long id);
}
