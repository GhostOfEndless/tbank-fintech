package com.example.entity.history;

import com.example.entity.Category;
import com.example.entity.CrudAction;

import java.time.LocalDateTime;

public record CategoryMemento(
        Long id,
        String slug,
        String name,
        LocalDateTime timestamp,
        CrudAction action
) {
    public static CategoryMemento createSnapshot(Category category, CrudAction action) {
        return new CategoryMemento(
                category.getId(),
                category.getSlug(),
                category.getName(),
                LocalDateTime.now(),
                action
        );
    }
}
