package com.example;

import com.example.entity.City;
import com.example.util.JsonFileUtil;
import com.example.util.impl.JsonFileUtilImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    private static final JsonFileUtil jsonUtil = new JsonFileUtilImpl();

    public static void main(String[] args) {
        log.info("Reading city.json...");
        var city = jsonUtil.readCityFromFile("city.json");
        printCity(city.orElse(null));

        log.info("Reading city-error.json...");
        var cityError = jsonUtil.readCityFromFile("city-error.json");
        printCity(cityError.orElse(null));

        log.info("Converting city object to xml format...");
        var content = jsonUtil.toXML(city.orElse(null));
        log.debug("Content is: {}", content);

        log.info("Saving string content to xml file...");
        jsonUtil.saveDataToFile(content, "city.xml");
    }

    private static void printCity(City city) {
        if (city != null) {
            log.info("Successfully read!");
            log.debug("City object is: {}", city);
        } else {
            log.warn("City object is empty!");
        }
    }
}
