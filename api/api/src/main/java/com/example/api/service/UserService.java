package com.example.api.service;

import com.example.api.entity.Role;
import com.example.api.entity.User;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.repository.RoleRepository;
import com.example.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    /***
     * Registra um novo usuário com papel padrão ROLE_USER.
     * Se o nome de usuário já existir, lança IllegalArgumentException.
     */
    @Transactional
    public User register(String username, String rawPassword) {
        if (userRepo.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }

        // Busca (ou cria) o papel padrão
        Role userRole = roleRepo.findByName("ROLE_USER")
                .orElseGet(() -> roleRepo.save(Role.builder().name("ROLE_USER").build()));

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword)) // hash BCrypt
                .roles(Set.of(userRole))
                .build();

        return userRepo.save(user);
    }

    /*** Carrega usuário pelo username (usado pelo Spring Security) ***/
    public User loadUserByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /*** Método utilitário para conceder papéis adicionais ***/
    @Transactional
    public void addRoleToUser(Long userId, String roleName) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Role role = roleRepo.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        user.getRoles().add(role);
    }
}