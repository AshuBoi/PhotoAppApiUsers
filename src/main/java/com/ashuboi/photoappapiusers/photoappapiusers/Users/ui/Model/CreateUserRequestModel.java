package com.ashuboi.photoappapiusers.photoappapiusers.Users.ui.Model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequestModel {
    @NotNull(message = "First name cannot be null")
    @Size(min=2, message = "First name cannot be less than two characters")
    private String firstName;

    @NotNull(message = "Last name cannot be null")
    @Size(min=2, message = "Last name cannot be less than two characters")
    private String lastName;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Not a Valid Email")
    private String email;

    @NotNull(message = "Password cannot be Null")
    @Size(min=8,max=16, message = "Password cannot be less than 8 or more than 16")
    private String password;
}
