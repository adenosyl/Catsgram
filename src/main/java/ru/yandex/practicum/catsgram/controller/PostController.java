package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;
import ru.yandex.practicum.catsgram.exception.NotFoundException;

import java.util.Collection;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Collection<Post> findAll() {
        return postService.findAll();
    }

    @PostMapping
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post post) {
        return postService.update(post);
    }

    @GetMapping("/{id}")
    public Post getPostById(@PathVariable Long id) {
        return postService.findAll().stream()
                .filter(post -> post.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пост с id = " + id + " не найден"));
    }

}