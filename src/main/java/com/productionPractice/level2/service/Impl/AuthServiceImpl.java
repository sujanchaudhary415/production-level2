package com.productionPractice.level2.service.Impl;

import com.productionPractice.level2.entity.Role;
import com.productionPractice.level2.entity.User;
import com.productionPractice.level2.enums.AppRole;
import com.productionPractice.level2.exception.DuplicateErrorException;
import com.productionPractice.level2.exception.ResourceNotFoundException;
import com.productionPractice.level2.repository.RoleRepository;
import com.productionPractice.level2.repository.UserRepository;
import com.productionPractice.level2.security.jwt.JwtUtils;
import com.productionPractice.level2.security.request.LoginRequest;
import com.productionPractice.level2.security.request.SignUpRequest;
import com.productionPractice.level2.security.response.AuthResponse;
import com.productionPractice.level2.security.services.UserDetailsImpl;
import com.productionPractice.level2.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    @Override
    public AuthResponse signin(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

        String token = jwtUtils.generateTokenFromUserName(user);

        List<String> roles = user.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .toList();

        return new AuthResponse(
                token,
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles
        );
    }

    @Override
    public String signup(SignUpRequest request) {

        if (userRepository.existsByUserName(request.getUserName())) {
            throw new DuplicateErrorException("User already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateErrorException("Email already exists");
        }

        User user = new User();
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));

        Set<Role> roles = new HashSet<>();

        Set<String> inputRoles = request.getRole();

        if (inputRoles == null || inputRoles.isEmpty()) {

            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Role", "name", AppRole.ROLE_USER));

            roles.add(userRole);

        } else {

            for (String r : inputRoles) {

                switch (r.toLowerCase()) {

                    case "admin":
                        roles.add(roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "Role", "name", AppRole.ROLE_ADMIN)));
                        break;

                    case "seller":
                        roles.add(roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "Role", "name", AppRole.ROLE_SELLER)));
                        break;

                    default:
                        roles.add(roleRepository.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "Role", "name", AppRole.ROLE_USER)));
                }
            }
        }

        user.setRoles(roles);
        userRepository.save(user);

        return "User registered successfully";
    }

}