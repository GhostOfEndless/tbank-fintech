package com.example.client;

import com.example.Application;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(classes = Application.class)
@TestPropertySource(properties = {
        "spring.cache.type=none"
})
public class CBRServiceIT {

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Autowired
    private CBRService cbrService;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("cbr.base-url", wireMockServer::baseUrl);
    }

    @BeforeEach
    void setUp() {
        wireMockServer.resetAll();
    }

    @Test
    @DisplayName("Should return valid currency rates")
    void getValCurs_success() {
        wireMockServer.stubFor(get(urlEqualTo("/XML_daily.asp"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBodyFile("XML_daily.xml")));

        var result = cbrService.getValCurs();

        assertThat(result)
                .isPresent()
                .hasValueSatisfying(valCurs -> assertThat(valCurs.getValutes())
                        .isNotEmpty()
                        .hasSize(45)
                        .filteredOn(v -> "USD".equals(v.getCharCode()))
                        .singleElement()
                        .isNotNull());
    }

    @Test
    @DisplayName("Should return empty when server returns 500")
    void getValCurs_serverErrorResponse() {
        wireMockServer.stubFor(get(urlEqualTo("/XML_daily.asp"))
                .willReturn(aResponse().withStatus(500)));

        var result = cbrService.getValCurs();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when network error occurs")
    void getValCurs_networkError() {
        wireMockServer.stubFor(get(urlEqualTo("/XML_daily.asp"))
                .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

        var result = cbrService.getValCurs();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return valid currency information")
    void getValuta_success() {
        wireMockServer.stubFor(get(urlEqualTo("/XML_valFull.asp"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBodyFile("XML_valFull.xml")));

        var result = cbrService.getValuta();

        assertThat(result)
                .isPresent()
                .hasValueSatisfying(valuta ->
                        assertThat(valuta.getItems())
                                .isNotEmpty()
                                .hasSize(72)
                                .filteredOn(i -> "USD".equals(i.getIsoCharCode()))
                                .singleElement()
                                .isNotNull());
    }

    @Test
    @DisplayName("Should return empty when server returns 500")
    void getValuta_failure() {
        wireMockServer.stubFor(get(urlEqualTo("/XML_valFull.asp"))
                .willReturn(aResponse().withStatus(500)));

        var result = cbrService.getValuta();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when network error occurs")
    void getValuta_networkError() {
        wireMockServer.stubFor(get(urlEqualTo("/XML_valFull.asp"))
                .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

        var result = cbrService.getValuta();

        assertThat(result).isEmpty();
    }
}
