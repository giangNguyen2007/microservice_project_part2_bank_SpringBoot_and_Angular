package gng.learning.userapi.controllers;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostUserDto {

    @NotBlank
    public String name;

    @Email
    public String email;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters long")
    public String password;

    public PostUserDto(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
