package com.example.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @JsonProperty("ID")
    private String id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("EngName")
    private String engName;

    @JsonProperty("Nominal")
    private Integer nominal;

    @JsonProperty("ParentCode")
    private String parentCode;

    @JsonProperty("ISO_Num_Code")
    private Integer isoNumCode;

    @JsonProperty("ISO_Char_Code")
    private String isoCharCode;
}
