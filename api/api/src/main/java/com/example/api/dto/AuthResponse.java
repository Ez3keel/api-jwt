package com.example.api.dto;

import lombok.*;

@Getter @Setter @AllArgsConstructor
public class AuthResponse {
    private String token;           // JWT retornado ao cliente
}