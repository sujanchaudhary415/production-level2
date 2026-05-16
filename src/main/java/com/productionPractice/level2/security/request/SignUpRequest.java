package com.productionPractice.level2.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SignUpRequest {
    @NotBlank(message = "User name can not be blank")
    @Size(min = 5,max = 15,message = "User name must be between 5 to 15 character")
    private String userName;

    @NotBlank(message = "Email can not be blank")
    @Size(max=50)
    @Email
    private String email;

    private Set<String> role;

    @NotBlank
    @Size(min=5,max=40,message = "Password size must be between 5 to 40 characters")
    private String password;

}
