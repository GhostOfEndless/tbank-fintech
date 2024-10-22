package com.example.repository.inmemory;

import com.example.entity.Category;
import com.example.repository.inmemory.impl.InMemoryRepositoryImpl;
import org.springframework.stereotype.Repository;


@Repository
public class CategoryInMemoryRepository extends InMemoryRepositoryImpl<Category> {

}
