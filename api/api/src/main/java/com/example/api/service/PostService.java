// src/main/java/com/example/api/service/PostService.java
package com.example.api.service;

import com.example.api.dto.*;
import com.example.api.entity.*;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepo;
    private final UserRepository userRepo;

    /*** Cria um post associado ao usuário autenticado ***/
    @Transactional
    public PostResponse createPost(Long userId, PostRequest request) {
        User author = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(author)
                .build();

        Post saved = postRepo.save(post);
        return mapToResponse(saved);
    }

    /*** Lista todos os posts (públicos) ***/
    public List<PostResponse> getAll() {
        return postRepo.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /*** Busca posts de um autor específico ***/
    public List<PostResponse> getByAuthor(Long authorId) {
        return postRepo.findByAuthorId(authorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /*** Atualiza um post – só o autor pode atualizar ***/
    @Transactional
    public PostResponse updatePost(Long postId, Long userId, PostRequest request) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new SecurityException("You are not the owner of this post");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        return mapToResponse(post);
    }

    /*** Deleta um post – só o autor pode remover ***/
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new SecurityException("You are not the owner of this post");
        }
        postRepo.delete(post);
    }

    // ---------- Helper ----------
    private PostResponse mapToResponse(Post p) {
        return PostResponse.builder()
                .id(p.getId())
                .title(p.getTitle())
                .content(p.getContent())
                .createdAt(p.getCreatedAt())
                .authorUsername(p.getAuthor().getUsername())
                .build();
    }
}
