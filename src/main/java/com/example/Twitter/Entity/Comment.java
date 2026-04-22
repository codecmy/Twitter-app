package com.example.Twitter.Entity;

import com.example.Twitter.Enums;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
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
public class Comment{
    @Id
    @UuidGenerator
    private String id;
    @Column(nullable = false)
    private String postIdFk;
    @Column(nullable = false)
    private String authorId;
    @Column(nullable = false,length = 300)
    private String content;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @CreatedDate
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Min(0)
    @Max(20)
    private int depth_level=0;

    @Column
    private Enums.WrittenByEnum writtenBy;

    @Column(nullable = false,updatable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
