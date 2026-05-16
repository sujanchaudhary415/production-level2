package com.productionPractice.level2.security.response;



import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String tokenType;
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
}