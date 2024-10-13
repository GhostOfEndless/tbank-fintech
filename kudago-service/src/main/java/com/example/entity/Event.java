package com.example.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor
public class Event {

    private static final Pattern PRICE_PATTERN = Pattern.compile("\\d+(?:\\s?\\d+)*");

    private Long id;
    private String title;
    private String price;
    @JsonProperty("is_free")
    private boolean isFree;

    public boolean isFitsBudget(Float budget) {
        if (isFree) {
            return true;
        }

        if (price == null || price.isEmpty()) {
            return false;
        }

        Integer extractedPrice = extractPrice(price);
        if (extractedPrice == null) {
            return false;
        }

        return extractedPrice <= budget;
    }

    private Integer extractPrice(String priceString) {
        Matcher matcher = PRICE_PATTERN.matcher(priceString);
        if (matcher.find()) {
            try {
                String priceStr = matcher.group().replaceAll("\\s", "");
                return Integer.parseInt(priceStr);
            } catch (NumberFormatException e) {
                // Игнорируем некорректные числа
            }
        }
        return null;
    }
}
