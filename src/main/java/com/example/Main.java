package com.example;

import com.example.collection.CustomLinkedList;
import com.example.entity.City;
import com.example.util.JsonFileUtil;
import com.example.util.impl.JsonFileUtilImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class Main {

    private static final JsonFileUtil jsonUtil = new JsonFileUtilImpl();

    public static void main(String[] args) {
        log.info("****************** Task 1 *********************");
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

        log.info("******************* Task 2 *********************");
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        list.add(1);
        list.get(0);
        list.remove(0);
        list.contains(1);
        list.addAll(List.of(1, 2, 3));
        log.info("Example list contains: {}", list);

        list = Stream.of(4, 5, 6)
                .reduce(new CustomLinkedList<>(), (customList, item) -> {
                    customList.add(item);
                    return customList;
                }, (list1, list2) -> list1);

        log.info("New list with data from stream: {}", list);
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
