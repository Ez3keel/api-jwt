
package com.example.api.controller;

import com.example.api.dto.*;
import com.example.api.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /*** Cria um post – usuário autenticado ***/
    @PostMapping
    public ResponseEntity<PostResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PostRequest request) {

        // O username vem da autenticação JWT
        Long userId = ((com.example.api.entity.User) userDetails).getId();

        PostResponse created = postService.createPost(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /*** Lista todos os posts (públicos) ***/
    @GetMapping
    public List<PostResponse> listAll() {
        return postService.getAll();
    }

    /*** Lista posts de um autor específico (por id) ***/
    @GetMapping("/author/{authorId}")
    public List<PostResponse> byAuthor(@PathVariable Long authorId) {
        return postService.getByAuthor(authorId);
    }

    /*** Atualiza um post – só o autor pode ***/
    @PutMapping("/{postId}")
    public PostResponse update(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PostRequest request) {

        Long userId = ((com.example.api.entity.User) userDetails).getId();
        return postService.updatePost(postId, userId, request);
    }

    /*** Deleta um post – só o autor pode ***/
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = ((com.example.api.entity.User) userDetails).getId();
        postService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();
    }
}

/**
 * AuthenticationPrincipal injeta o objeto autenticado (nosso User).
 * O service verifica se o userId corresponde ao autor antes de atualizar/remover.
 */