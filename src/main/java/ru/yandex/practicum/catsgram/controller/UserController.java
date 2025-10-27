package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail() == null) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        if (emailExists(user.getEmail())) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (user.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        User existing = users.get(user.getId());
        if (existing == null) {
            throw new ConditionsNotMetException("Пользователь не найден");
        }
        if (user.getEmail() != null && !user.getEmail().equals(existing.getEmail()) && emailExists(user.getEmail())) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        // Обновляем только непустые поля
        if (user.getEmail() != null) existing.setEmail(user.getEmail());
        if (user.getUsername() != null) existing.setUsername(user.getUsername());
        if (user.getPassword() != null) existing.setPassword(user.getPassword());
        return existing;
    }

    private boolean emailExists(String email) {
        return users.values().stream().anyMatch(u -> email.equals(u.getEmail()));
    }

    private long getNextId() {
        return users.keySet().stream().mapToLong(Long::longValue).max().orElse(0L) + 1;
    }
}
