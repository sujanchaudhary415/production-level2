package com.productionPractice.level2.config;

import com.productionPractice.level2.entity.Role;
import com.productionPractice.level2.enums.AppRole;
import com.productionPractice.level2.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {

        for (AppRole role : AppRole.values()) {

            if (roleRepository.findByRoleName(role).isEmpty()) {
                Role r = new Role();
                r.setRoleName(role);
                roleRepository.save(r);
            }
        }

        System.out.println("Roles seeded successfully");
    }
}