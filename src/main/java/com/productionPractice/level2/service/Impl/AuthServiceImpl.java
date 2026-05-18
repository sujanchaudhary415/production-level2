package com.productionPractice.level2.service.Impl;

import com.productionPractice.level2.entity.RefreshToken;
import com.productionPractice.level2.entity.Role;
import com.productionPractice.level2.entity.User;
import com.productionPractice.level2.repository.UserRepository;
import com.productionPractice.level2.security.response.LoginResult;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final PasswordEncoder encoder;
    private final CommonHelper commonHelper;
    private final AuthHelper authHelper;

    @Override
    @Transactional
    public AuthResponse signup(SignUpRequest request) {

        String username = commonHelper.normalize(request.getUserName());
        String email = commonHelper.normalizeEmail(request.getEmail());

        authHelper.validateDuplicateUser(username, email);

        Set<Role> roles = authHelper.mapRolesFromStrings(request.getRole());

        User user = new User();
        user.setUserName(username);
        user.setEmail(email);
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        return new AuthResponse(
                savedUser.getUserId(),
                savedUser.getUserName(),
                savedUser.getEmail(),
                savedUser.getRoles()
                        .stream()
                        .map(role -> role.getRoleName().name())
                        .toList()
        );
    }

    @Override
    public LoginResult signin(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        AuthResponse authResponse = new AuthResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
        );

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        ResponseCookie refreshCookie = jwtUtils.generateRefreshCookie(refreshToken.getToken());

        return new LoginResult(authResponse, jwtCookie, refreshCookie);
    }

    @Override
    public ResponseCookie refreshAccessToken(String refreshTokenFromCookie) {
        if (refreshTokenFromCookie == null || refreshTokenFromCookie.isBlank()) {
            throw new IllegalArgumentException("Refresh token cookie is missing.");
        }

        return refreshTokenService.findByToken(refreshTokenFromCookie)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                            .collect(Collectors.toList());

                    UserDetailsImpl userDetails = new UserDetailsImpl(
                            user.getUserId(),
                            user.getUserName(),
                            user.getEmail(),
                            null,
                            authorities
                    );

                    return jwtUtils.generateJwtCookie(userDetails);
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is missing from the database."));
    }


    @Override
    @Transactional(readOnly = true)
    public String currentUserName(Authentication authentication) {
        return (authentication != null) ? authentication.getName() : "null";
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse getUserDetails(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            throw new IllegalArgumentException("Unauthorized context access attempt");
        }

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new AuthResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
        );
    }
}