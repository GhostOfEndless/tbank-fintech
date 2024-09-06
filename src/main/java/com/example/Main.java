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
        testJson();

        log.info("******************* Task 2 *********************");
        testList();
    }

    private static void testList() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        log.info("Addition first element '1' to list. Result is {}",
                list.add(1));
        log.info("First element after this: {}",
                list.get(0));
        log.info("Removing element '{}' from list...",
                list.remove(0));
        log.info("Is list contains '1'? - {}",
                list.contains(1));
        log.info("Adding to list numbers '1, 2, 3'. Result is {}",
                list.addAll(List.of(1, 2, 3)));
        log.info("Now list contains: {}", list);

        log.info("Changing list with Stream reduce...");
        list = Stream.of(4, 5, 6)
                .reduce(new CustomLinkedList<>(), (customList, item) -> {
                    customList.add(item);
                    return customList;
                }, (list1, list2) -> list1);

        log.info("New list with data from stream: {}", list);
    }

    private static void testJson() {
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
