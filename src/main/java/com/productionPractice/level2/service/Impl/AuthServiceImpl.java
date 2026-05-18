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
import com.productionPractice.level2.service.helper.AuthHelper;
import com.productionPractice.level2.service.helper.CommonHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
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
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);
        List<String> roles = user.getAuthorities().stream().map(a -> a.getAuthority()).toList();

        return new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), roles, jwtCookie);
    }

    @Override
    public String signup(SignUpRequest request) {

        String username = commonHelper.normalize(request.getUserName());
        String email = commonHelper.normalizeEmail(request.getEmail());
        authHelper.validateDuplicateUser(username, email);

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

    @Override
    public String currentUserName(Authentication authentication) {
        if(authentication!=null) {
            return authentication.getName();}
        else {
            return "null";
        }
    }

    @Override
    public AuthResponse getUserDetails(Authentication authentication) {

        UserDetailsImpl userDetails =
                (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .toList();

        return new AuthResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles,
                null
        );
    }
}