package com.example.repository.specification;

import com.example.entity.Event;
import com.example.entity.Location;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class EventSpecifications {

    public static Specification<Event> withDynamicQuery(String name, Location location,
                                                        Instant fromDate, Instant toDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            root.fetch("location", JoinType.LEFT);

            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if (location != null) {
                predicates.add(cb.equal(root.get("location"), location));
            }

            if (fromDate != null && toDate != null) {
                predicates.add(cb.between(root.get("startDate"), fromDate, toDate));
            } else if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), fromDate));
            } else if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startDate"), toDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
