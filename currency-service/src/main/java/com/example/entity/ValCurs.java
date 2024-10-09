package com.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ValCurs {

    @JsonFormat(pattern = "dd.MM.yyy")
    @JsonProperty("Date")
    private LocalDate date;

    @JsonProperty("name")
    private String name;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty("Valute")
    private List<Valute> valutes;
}