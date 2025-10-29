package ru.yandex.practicum.catsgram.controller;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {
    private String error;

    public ErrorResponse() {}

    public ErrorResponse(String error) {
        this.error = error;
    }

}