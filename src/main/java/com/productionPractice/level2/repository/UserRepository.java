package com.productionPractice.level2.repository;

import com.productionPractice.level2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUserName(String username);
    boolean existsByUserName(String userName);
    boolean existsByEmail( String email);
}
