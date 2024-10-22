package com.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_locations", schema = "kudago")
public class Location extends AbstractEntity {

    @Column(name = "c_slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "c_name", nullable = false)
    private String name;
}
