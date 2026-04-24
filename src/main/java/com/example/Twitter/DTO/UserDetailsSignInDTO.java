package com.example.Twitter.DTO;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDetailsSignInDTO{
    @NotBlank(message = "UserName cannot be empty")
    private String userName;
    @JsonAlias("isPremium")
    private Boolean premium;
}
