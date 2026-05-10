package com.nexus.NexusShip.repository;

import com.nexus.NexusShip.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {


    Optional<User> findByEmail(String email);

    Optional<User> findByNationalId(String nationalId);

    Optional<User> findByPhoneNumber(String phoneNumber);

    // finding a user even if deleted with native query to avoid @SQLRestriction("is_deleted = false")

    @Query(value = "select * from users where national_id =: nationalId", nativeQuery = true)
    Optional<User> findUserByNationalIdEverywhere(@Param("nationalId")String nationalId);
}
