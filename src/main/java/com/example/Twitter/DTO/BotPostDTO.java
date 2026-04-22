package com.example.Twitter.DTO;
import com.example.Twitter.Enums;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BotPostDTO{
    private Enums.WrittenByEnum writtenBy= Enums.WrittenByEnum.BOT;
    @NotBlank(message = "AuthorID cannot be blank")
    private String authorId;
    @NotBlank(message = "Content cannot be blank")
    private String content;
}
