package com.example.Twitter.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BotDetailsDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
}
