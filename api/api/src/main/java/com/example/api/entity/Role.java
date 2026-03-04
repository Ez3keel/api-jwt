package com.example.api.entity;


import jakarta.persistence.*;
import lombok.*;

//Papeis de usuário ex:. ROLE_USER, ROLE_ADMIN

@Entity
@Table(name = "roles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Role {

    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

}
