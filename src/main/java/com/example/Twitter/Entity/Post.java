package com.example.Twitter.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Post{
    @Id
    @UuidGenerator
    private String id;
    @Column(nullable = false)
    private String authorId;
    @Column(nullable = false)
    private String content;
    @CreatedDate
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @Min(0)
    private int userLikes=0;

    @Column(nullable = false)
    @Min(0)
    private int botLikes=0;

    @Column(nullable = false)
    @Min(0)
    private int botDislikes=0;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = true)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "bot_id", nullable = true)
    @JsonIgnore
    private Bot bot;
}
