package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.service.SortOrder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.*;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<Post> findAll(
            @RequestParam(defaultValue = "desc") String sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        SortOrder sortOrder = SortOrder.from(sort);
        if (sortOrder == null) {
            throw new ParameterNotValidException("sort", "Получено: " + sort + ", должно быть: asc или desc");
        }
        if (size <= 0) {
            throw new ParameterNotValidException("size", "Размер должен быть больше нуля");
        }
        if (from < 0) {
            throw new ParameterNotValidException("from", "Начало выборки должно быть положительным числом");
        }

        return postService.findAll(from, size, sortOrder);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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