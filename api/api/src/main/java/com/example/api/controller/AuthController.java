
package com.example.api.controller;

import com.example.api.dto.*;
import com.example.api.entity.User;
import com.example.api.service.UserService;
import com.example.api.config.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.crypto.password.*;
import org.springframework.web.bind.annotation.*;

import java.util.stream.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    /*** Registro de usuário ***/
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody AuthRequest request) {
        userService.register(request.getUsername(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    /*** Login – devolve JWT ***/
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        // Autentica usando o AuthenticationManager (Spring Security)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        // Recupera o usuário autenticado
        User user = (User) authentication.getPrincipal();

        // Converte papéis para lista de strings (ex.: ["ROLE_USER"])
        var roles = user.getRoles()
                .stream()
                .map(r -> r.getName())
                .collect(Collectors.toList());

        // Gera token
        String jwt = jwtUtils.generateToken(user.getUsername(), roles);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}


/**
 * Fluxo:
 * Cliente envia /api/auth/register → cria registro.
 * Cliente envia /api/auth/login com username/password → recebe JWT.
 * Em chamadas subsequentes, o cliente coloca o token no cabeçalho Authorization: Bearer <jwt>.
 */