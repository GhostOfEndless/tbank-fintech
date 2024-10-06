package com.example.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.io.IOException;

@Configuration
public class ApplicationConfig {

    @Bean
    public RestClient restClient(@Value("${cbr.base-url}") String url) {
        return RestClient.builder()
                .baseUrl(url)
                .defaultHeader("Accept", "application/xml")
                .messageConverters(converters -> converters.add(xmlConverter()))
                .build();
    }

    @Bean
    public MappingJackson2XmlHttpMessageConverter xmlConverter() {
        var floatModule = new SimpleModule("FloatDeserialization", Version.unknownVersion())
                .addDeserializer(Float.class, new JsonDeserializer<>() {
                    @Override
                    public Float deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                        return Float.parseFloat(p.getValueAsString().trim().replace(",", "."));
                    }
                });

        var mapper = XmlMapper.xmlBuilder()
                .addModule(new JavaTimeModule())
                .addModule(floatModule)
                .build();

        return new MappingJackson2XmlHttpMessageConverter(mapper);
    }
}
