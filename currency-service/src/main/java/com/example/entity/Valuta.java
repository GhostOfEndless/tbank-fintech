package com.example.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;

import java.util.List;

@Data
public class Valuta {

    @JsonProperty("name")
    private String name;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty("Item")
    private List<Item> items;
}
