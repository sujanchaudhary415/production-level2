package com.productionPractice.level2.service.Impl;

import com.productionPractice.level2.entity.Role;
import com.productionPractice.level2.entity.User;
import com.productionPractice.level2.repository.UserRepository;
import com.productionPractice.level2.security.jwt.JwtUtils;
import com.productionPractice.level2.security.request.LoginRequest;
import com.productionPractice.level2.security.request.SignUpRequest;
import com.productionPractice.level2.security.response.AuthResponse;
import com.productionPractice.level2.security.services.UserDetailsImpl;
import com.productionPractice.level2.service.AuthService;
import com.productionPractice.level2.service.helper.AuthHelper; // Injected
import com.productionPractice.level2.service.helper.CommonHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final CommonHelper commonHelper;
    private final AuthHelper authHelper; // FIX: Added AuthHelper

    @Override
    public AuthResponse signin(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        String token = jwtUtils.generateToken(user);
        List<String> roles = user.getAuthorities().stream().map(a -> a.getAuthority()).toList();

        return new AuthResponse(token, "Bearer", user.getId(), user.getUsername(), user.getEmail(), roles);
    }

    @Override
    public String signup(SignUpRequest request) {
        // 1. Normalize
        String username = commonHelper.normalize(request.getUserName());
        String email = commonHelper.normalizeEmail(request.getEmail());

        // 2. Validate (Delegated to Helper)
        authHelper.validateDuplicateUser(username, email);

        // 3. Resolve Database Roles (Delegated to Helper - Completely hides the switch-case!)
        Set<Role> roles = authHelper.mapRolesFromStrings(request.getRole());

        // 4. Map Entity & Save
        User user = new User();
        user.setUserName(username);
        user.setEmail(email);
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRoles(roles);

        userRepository.save(user);
        return "User registered successfully";
    }
}