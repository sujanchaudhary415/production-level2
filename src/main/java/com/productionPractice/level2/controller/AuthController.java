package com.productionPractice.level2.controller;

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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;

    // 🔐 SIGN IN
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@Valid @RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String token = jwtUtils.generateTokenFromUserName(userDetails);

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        AuthResponse response = new AuthResponse(
                token,
                "Bearer",
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
        );

        return ResponseEntity.ok(response);
    }

    // 📝 SIGN UP
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest request) {

        // check username
        if (userRepository.existsByUserName(request.getUserName())) {
            throw new DuplicateErrorException("User already exists");
        }

        // check email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateErrorException("Email already exists");
        }

        // create user
        User user = new User();
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));

        Set<String> strRoles = request.getRole();
        Set<Role> roles = new HashSet<>();

        // default role
        if (strRoles == null || strRoles.isEmpty()) {

            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Role", "name", AppRole.ROLE_USER)
                    );

            roles.add(userRole);

        } else {

            for (String role : strRoles) {

                switch (role.toLowerCase()) {

                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(() ->
                                        new ResourceNotFoundException("Role", "name", AppRole.ROLE_ADMIN)
                                );
                        roles.add(adminRole);
                        break;

                    case "seller":
                        Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                                .orElseThrow(() ->
                                        new ResourceNotFoundException("Role", "name", AppRole.ROLE_SELLER)
                                );
                        roles.add(sellerRole);
                        break;

                    default:
                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(() ->
                                        new ResourceNotFoundException("Role", "name", AppRole.ROLE_USER)
                                );
                        roles.add(userRole);
                        break;
                }
            }
        }

        // assign roles + save user
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }
}