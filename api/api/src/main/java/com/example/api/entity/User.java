package com.example.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;

//Um usuário pode ter muitos papéis -> relação ManyToMany

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id //PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;  //login

    @Column(nullable = false)
    private String password;  //hash BCrypt

    @ManyToMany(fetch = FetchType.EAGER) //Carrega os papéis junto ao usuário
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>(); // FK -> Role
}
