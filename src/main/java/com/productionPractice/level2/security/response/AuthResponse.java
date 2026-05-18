package com.productionPractice.level2.security.response;

import com.productionPractice.level2.enums.AppRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private final Long id;
    private final String username;
    private final String email;
    private final List<String> roles;


}