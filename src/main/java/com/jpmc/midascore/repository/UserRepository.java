package com.jpmc.midascore.repository;

import com.jpmc.midascore.entity.UserRecord;
import org.springframework.data.jpa.repository.JpaRepository;  // FIX: CrudRepository → JpaRepository
import org.springframework.stereotype.Repository;

@Repository  // Optional with JpaRepository
public interface UserRepository extends JpaRepository<UserRecord, Long> {  // Better base interface
  
    // findById(Long id) inherited from JpaRepository - REMOVE custom method
    UserRecord findByName(String name);  // Custom query method
}
