package com.example.util.impl;

import com.example.entity.City;
import com.example.util.JsonFileUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
public class JsonFileUtilImpl implements JsonFileUtil {

    private final ObjectMapper mapper = new ObjectMapper();
    private final XmlMapper xmlMapper = new XmlMapper();

    @Override
    public Optional<City> readCityFromFile(String filepath) {
        try {
            checkFilepath(filepath);
            var content = new String(Files.readAllBytes(Paths.get(filepath)));
            return Optional.of(mapper.readValue(content, City.class));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid filepath!");
        } catch (JsonMappingException e) {
            log.warn("Invalid JSON mapping!");
        } catch (JsonProcessingException e) {
            log.warn("Invalid JSON-file format!");
        } catch (IOException e) {
            log.error("Error occurred while reading JSON-file {}", filepath);
        }

        return Optional.empty();
    }

    @Override
    public String toXML(City city) {
        try {
            if (city == null) {
                log.warn("City object is null!");
                return "";
            }

            return xmlMapper.writeValueAsString(city);
        } catch (JsonProcessingException e) {
            log.warn("Error occurred while processing object!");
        }
        return "";
    }

    @Override
    public void saveDataToFile(String data, String filepath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            if (data.isBlank()) {
                log.warn("Blank content. Nothing to write");
                return;
            }
            writer.write(data);
            log.info("Content successfully wrote to {}", filepath);
        } catch (IOException e) {
            log.error("Error occurred while writing content to file {}", filepath);
        }
    }

    private void checkFilepath(String filepath) {
        if (filepath == null || filepath.isBlank()) {
            throw new IllegalArgumentException();
        }
    }
}
