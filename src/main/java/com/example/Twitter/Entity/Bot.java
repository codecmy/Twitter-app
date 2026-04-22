package com.example.Twitter.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Bot{
    @Id
    @UuidGenerator
    private String id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    @Size(min = 1, max = 100)
    private String personaDetails;
}
