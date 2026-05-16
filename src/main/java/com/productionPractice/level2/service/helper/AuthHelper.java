package com.productionPractice.level2.service.helper;

import com.productionPractice.level2.entity.Role;
import com.productionPractice.level2.enums.AppRole;
import com.productionPractice.level2.exception.DuplicateErrorException;
import com.productionPractice.level2.exception.ResourceNotFoundException;
import com.productionPractice.level2.repository.RoleRepository;
import com.productionPractice.level2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthHelper {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CommonHelper commonHelper;

    public void validateDuplicateUser(String username, String email) {
        if (userRepository.existsByUserName(username)) {
            throw new DuplicateErrorException("User already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateErrorException("Email already exists");
        }
    }

    public Set<Role> mapRolesFromStrings(Set<String> inputRoles) {
        Set<Role> roles = new HashSet<>();

        if (inputRoles == null || inputRoles.isEmpty()) {
            roles.add(getRoleOrThrow(AppRole.ROLE_USER));
            return roles;
        }

        for (String r : inputRoles) {
            String normalizedRole = commonHelper.normalize(r).toLowerCase();
            switch (normalizedRole) {
                case "admin" -> roles.add(getRoleOrThrow(AppRole.ROLE_ADMIN));
                case "seller" -> roles.add(getRoleOrThrow(AppRole.ROLE_SELLER));
                default -> roles.add(getRoleOrThrow(AppRole.ROLE_USER));
            }
        }
        return roles;
    }

    private Role getRoleOrThrow(AppRole appRole) {
        return roleRepository.findByRoleName(appRole)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", appRole.name()));
    }
}