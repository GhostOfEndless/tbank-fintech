package com.example.repository.impl;

import com.example.entity.AbstractEntity;
import com.example.repository.InMemoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryRepositoryImpl <V extends AbstractEntity> implements InMemoryRepository<Long, V> {

    private final ConcurrentHashMap<Long, V> storage = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    @Override
    public List<V> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<V> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public V save(V entity) {
        if (entity != null) {
            if (entity.getId() != null && storage.containsKey(entity.getId())) {
                storage.put(entity.getId(), entity);
            } else {
                Long id = idCounter.incrementAndGet();
                entity.setId(id);
                storage.put(id, entity);
            }
            return entity;
        } else {
            throw new IllegalArgumentException("Entity cannot be null!");
        }
    }

    @Override
    public void delete(Long id) {
        storage.remove(id);
    }
}
