package com.example.repository;

import com.example.entity.Location;
import com.example.repository.impl.InMemoryRepositoryImpl;
import org.springframework.stereotype.Repository;

@Repository
public class LocationRepository extends InMemoryRepositoryImpl<Location> {

}
