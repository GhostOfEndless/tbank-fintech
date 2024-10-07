package com.example.controller;

import com.example.Application;
import com.example.controller.dto.ConvertCurrencyDTO;
import com.example.controller.dto.CurrencyRateDTO;
import com.example.controller.payload.ConvertCurrencyPayload;
import com.example.entity.ValCurs;
import com.example.entity.Valute;
import com.example.exception.CurrencyNotFoundException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = Application.class)
@TestPropertySource(properties = {
        "spring.cache.type=none"
})
public class CurrencyRestControllerIT {

    private static final String uri = "/api/v1/currencies";

    @Container
    static WireMockContainer wiremockServer = new WireMockContainer("wiremock/wiremock:2.35.1-1-alpine")
            .withFileFromResource("XML_daily.xml", "__files/XML_daily.xml")
            .withMappingFromResource("daily", CurrencyRestControllerIT.class, "daily.json")
            .withFileFromResource("XML_valFull.xml", "__files/XML_valFull.xml")
            .withMappingFromResource("valFull", CurrencyRestControllerIT.class, "valFull.json");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("cbr.base-url", wiremockServer::getBaseUrl);
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    private static XmlMapper xmlMapper;

    private ValCurs valCurs;

    @BeforeAll
    static void xmlSetup() {
        var floatModule = new SimpleModule("FloatDeserialization", Version.unknownVersion())
                .addDeserializer(Float.class, new JsonDeserializer<>() {
                    @Override
                    public Float deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                        return Float.parseFloat(p.getValueAsString().trim().replace(",", "."));
                    }
                });

        xmlMapper = XmlMapper.xmlBuilder()
                .addModule(new JavaTimeModule())
                .addModule(floatModule)
                .build();
    }

    @BeforeEach
    void setup() {
        try (var inputStream = getClass().getClassLoader().getResourceAsStream("__files/XML_daily.xml")) {
            assertThat(inputStream).isNotNull();
            valCurs = xmlMapper.readValue(inputStream, ValCurs.class);
        } catch (Exception e) {
            log.error("Error while reading file! {}", e.getMessage());
        }
    }

    @Test
    @SneakyThrows
    void getCurrencyRate_success() {
        var valute = valCurs.getValutes().getFirst();

        var mvcResponse = mockMvc.perform(MockMvcRequestBuilders
                        .get(uri + "/rate/%s".formatted(valute.getCharCode())))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        var expectedContent = CurrencyRateDTO.builder()
                .currency(valute.getCharCode())
                .rate(valute.getVunitRate())
                .build();

        var content = objectMapper.readValue(mvcResponse.getContentAsString(), CurrencyRateDTO.class);

        assertThat(content).isEqualTo(expectedContent);
    }

    @ParameterizedTest
    @SneakyThrows
    @ValueSource(strings = {"111", "AaA", "RU", "A7A"})
    void getCurrencyRate_invalidFilepath(String path) {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(uri + "/rate/%s".formatted(path)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse();
    }

    @ParameterizedTest
    @SneakyThrows
    @ValueSource(strings = {"ATS", "AON", "BEF", "GRD"})
    void getCurrencyRate_notFoundCurrency(String path) {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(uri + "/rate/%s".formatted(path)))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse();
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"CNY", "BYN", "RUB"})
    void convertCurrency_success(String toValue) {
        var fromValute = getValuteFromList("USD");
        var toValute = getValuteFromList(toValue);

        var payload = ConvertCurrencyPayload.builder()
                .fromCurrency(fromValute.getCharCode())
                .toCurrency(toValute.getCharCode())
                .amount(100.f)
                .build();

        var mvcResponse = mockMvc.perform(MockMvcRequestBuilders
                        .post(uri + "/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        var convertedAmount = payload.amount() * fromValute.getVunitRate() / toValute.getVunitRate();
        var expectedContent = ConvertCurrencyDTO.builder()
                .fromCurrency(fromValute.getCharCode())
                .toCurrency(toValute.getCharCode())
                .convertedAmount(convertedAmount)
                .build();

        var content = objectMapper.readValue(mvcResponse.getContentAsString(), ConvertCurrencyDTO.class);

        assertThat(content).isEqualTo(expectedContent);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getInvalidConvertPayloads")
    void convertCurrency_invalidPayload(ConvertCurrencyPayload payload) {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(uri + "/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse();
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getNotProvidedCurrencyConvertPayloads")
    void convertCurrency_notProvidedCurrency(ConvertCurrencyPayload payload) {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(uri + "/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse();
    }

    @Test
    @SneakyThrows
    void getCurrencyRate_serviceUnavailable() {
        wiremockServer.stop();

        mockMvc.perform(MockMvcRequestBuilders
                        .get(uri + "/rate/USD"))
                .andExpectAll(
                        status().isServiceUnavailable(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse();

        wiremockServer.start();
    }

    private static Stream<Arguments> getInvalidConvertPayloads() {
        return Stream.of(
                Arguments.of(new ConvertCurrencyPayload(null, null, null)),
                Arguments.of(new ConvertCurrencyPayload("", "", 1.0f)),
                Arguments.of(new ConvertCurrencyPayload("AAA", "", 1.0f)),
                Arguments.of(new ConvertCurrencyPayload("aaa", "AAA", 1.0f)),
                Arguments.of(new ConvertCurrencyPayload("AAA", "AAA", null)),
                Arguments.of(new ConvertCurrencyPayload("AAA", "AAA", 0.0f)),
                Arguments.of(new ConvertCurrencyPayload("AAA", "AAA", -1.0f)),
                Arguments.of(new ConvertCurrencyPayload("", "AAA", 1.0f)),
                Arguments.of(new ConvertCurrencyPayload("AAA", "AAA", 1.0f))
        );
    }

    private static Stream<Arguments> getNotProvidedCurrencyConvertPayloads() {
        return Stream.of(
                Arguments.of(new ConvertCurrencyPayload("USD", "AON", 100.0f)),
                Arguments.of(new ConvertCurrencyPayload("USD", "ATS", 100.0f)),
                Arguments.of(new ConvertCurrencyPayload("USD", "BEF", 100.0f)),
                Arguments.of(new ConvertCurrencyPayload("USD", "GRD", 100.0f))
        );
    }

    private Valute getValuteFromList(String valuteCode) {
        return valCurs.getValutes().stream()
                .filter(valute -> valute.getCharCode().equals(valuteCode))
                .findFirst()
                .orElseThrow(() -> new CurrencyNotFoundException(valuteCode));
    }
}
