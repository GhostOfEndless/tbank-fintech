package com.example.repository;

import java.util.List;
import java.util.Optional;

public interface InMemoryRepository<K, V> {

    List<V> findAll();

    Optional<V> findById(K id);

    V save(V entity);

    void delete(K id);

    boolean existsById(K id);
}
