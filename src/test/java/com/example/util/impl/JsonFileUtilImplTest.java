package com.example.util.impl;

import com.example.entity.City;
import com.example.entity.Coordinates;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
class JsonFileUtilImplTest {

    private JsonFileUtilImpl jsonFileUtil;
    private ObjectMapper objectMapper;
    private XmlMapper xmlMapper;

    @BeforeEach
    void setUp() {
        objectMapper = mock(ObjectMapper.class);
        xmlMapper = mock(XmlMapper.class);
        jsonFileUtil = new JsonFileUtilImpl(objectMapper, xmlMapper);
    }

    @Test
    @DisplayName("Read city from JSON file successfully")
    void testReadCityFromFileSuccess() throws Exception {
        String filepath = "city.json";
        City expectedCity = new City("spb", new Coordinates(59.939095f, 30.315868f));
        when(objectMapper.readValue(any(String.class), eq(City.class))).thenReturn(expectedCity);

        Optional<City> result = jsonFileUtil.readCityFromFile(filepath);

        assertThat(result).isPresent().contains(expectedCity);
    }

    @Test
    @DisplayName("Fail to read city from JSON file due to invalid JSON")
    void testReadCityFromFileInvalidJson() throws Exception {
        String filepath = "city-error.json";
        when(objectMapper.readValue(any(String.class), eq(City.class)))
                .thenThrow(new JsonMappingException(null, "Test error"));

        Optional<City> result = jsonFileUtil.readCityFromFile(filepath);

        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("Convert City object to XML successfully")
    void testToXMLSuccess() throws Exception {
        City city = new City("spb", new Coordinates(59.939095f, 30.315868f));
        String expectedXml = "<City><slug>spb</slug><coords><lat>59.939095</lat><lon>30.315868</lon></coords></City>";
        when(xmlMapper.writeValueAsString(city)).thenReturn(expectedXml);

        String result = jsonFileUtil.toXML(city);

        assertThat(result).isEqualTo(expectedXml);
    }

    @Test
    @DisplayName("Save data to file successfully")
    void testSaveDataToFileSuccess(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("city.xml");
        String data = "some data";

        jsonFileUtil.saveDataToFile(data, tempFile.toString());

        assertThat(Files.exists(tempFile)).isTrue();
        String content = new String(Files.readAllBytes(tempFile));
        assertThat(content).isEqualTo(data);
    }
}
