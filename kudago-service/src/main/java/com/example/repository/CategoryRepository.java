package com.example.repository;

import com.example.entity.Category;
import com.example.repository.impl.InMemoryRepositoryImpl;
import org.springframework.stereotype.Repository;


@Repository
public class CategoryRepository extends InMemoryRepositoryImpl<Category> {

}
