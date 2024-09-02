package com.example.util;

import com.example.entity.City;

import java.util.Optional;

public interface JsonFileUtil {

    Optional<City> readCityFromFile(String filepath);

    String toXML(City city);

    void saveDataToFile(String data, String filepath);
}
