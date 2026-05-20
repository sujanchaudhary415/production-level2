package com.productionPractice.level2.repository;

import com.productionPractice.level2.entity.Role;
import com.productionPractice.level2.enums.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Integer> {
    Optional<Role> findByRoleName(AppRole appRole);
}
