package com.productionPractice.level2.security.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseCookie;
import java.util.List;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private final Long id;
    private final String username;
    private final String email;
    private final List<String> roles;

    @JsonIgnore // Production Tip: Prevents the raw cookie configuration from leaking into the JSON body response
    private final ResponseCookie jwtCookie;
}