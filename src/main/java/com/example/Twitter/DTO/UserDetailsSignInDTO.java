package com.example.Twitter.DTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDetailsSignInDTO{
    @NotBlank(message = "UserName cannot be empty")
    private String userName;
    @NotBlank(message = "Is user premium or not")
    private boolean isPremium;
}
