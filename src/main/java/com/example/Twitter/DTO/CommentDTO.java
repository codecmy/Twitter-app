package com.example.Twitter.DTO;

import com.example.Twitter.Enums;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDTO{
    @NotBlank
    private String postId;
    @NotBlank
    private String authorId;
    @NotBlank
    private String content;
    private Enums.WrittenByEnum writtenBy;
    private String parentCommentId;
}
