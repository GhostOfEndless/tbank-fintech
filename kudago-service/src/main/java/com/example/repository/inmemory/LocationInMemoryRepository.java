package com.example.repository.inmemory;

import com.example.entity.Location;
import com.example.repository.inmemory.impl.InMemoryRepositoryImpl;
import org.springframework.stereotype.Repository;

@Repository
public class LocationInMemoryRepository extends InMemoryRepositoryImpl<Location> {

}
