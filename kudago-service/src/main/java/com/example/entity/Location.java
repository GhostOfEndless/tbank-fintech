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
//@Entity
//@Table(name = "t_locations", schema = "kudago")
public class Location extends AbstractEntity {

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("name")
    private String name;
}
