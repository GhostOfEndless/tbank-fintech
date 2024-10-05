package com.example.controller;

import com.example.controller.dto.ConvertCurrencyDTO;
import com.example.controller.dto.CurrencyRateDTO;
import com.example.controller.payload.ConvertCurrencyPayload;
import com.example.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/currencies")
public class CurrencyRestController {

    private final CurrencyService currencyService;

    @GetMapping("/rate/{code}")
    public CurrencyRateDTO getCurrencyRate(@PathVariable String code) {
        var rate = currencyService.getCurrencyRate(code);
        return CurrencyRateDTO.builder()
                .currency(code)
                .rate(rate)
                .build();
    }

    @PostMapping("/convert")
    public ConvertCurrencyDTO convertCurrency(@RequestBody ConvertCurrencyPayload payload) {
        return ConvertCurrencyDTO.builder().build();
    }
}
