package com.example.api.dto;

import lombok.*;
import java.time.*;

@Getter @Setter @AllArgsConstructor @Builder
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String authorUsername;
}