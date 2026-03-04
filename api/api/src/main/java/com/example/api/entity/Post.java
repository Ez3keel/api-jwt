package com.example.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

/**
 * Resumo das chaves:
 * User.id, Role.id, Post.id → PKs (auto increment).
 * Tabela users_roles (join) contém duas FK (user_id, role_id).
 * posts.user_id → FK para users.id.
 */

@Entity
@Table(name = "posts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Post {

    @Id                                          // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // FK → User (autor do post)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // coluna FK
    private User author;
}
