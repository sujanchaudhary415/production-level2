package com.productionPractice.level2.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {

    @NotBlank(message = "user name must not be blank")
    @Size(min=5,max=15,message = "User name must be between 5 and 15")
    private String userName;

    @NotBlank(message = "Email must not be blank")
    @Email
    @Size(max=50)
    private String email;

    @NotBlank(message = "Email must not be blank")
    @Size(max=120)
    private String password;

}
