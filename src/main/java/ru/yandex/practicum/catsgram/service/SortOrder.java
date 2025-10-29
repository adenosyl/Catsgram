package ru.yandex.practicum.catsgram.service;

public enum SortOrder {
    ASCENDING, DESCENDING;

    public static SortOrder from(String order) {
        if (order == null) return DESCENDING; // значение по умолчанию
        switch (order.toLowerCase()) {
            case "ascending":
            case "asc":
                return ASCENDING;
            case "descending":
            case "desc":
                return DESCENDING;
            default:
                throw new IllegalArgumentException("Неверное значение параметра sort: " + order);
        }
    }
}