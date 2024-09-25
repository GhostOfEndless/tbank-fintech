package com.example.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Location extends AbstractEntity {

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("name")
    private String name;
}
