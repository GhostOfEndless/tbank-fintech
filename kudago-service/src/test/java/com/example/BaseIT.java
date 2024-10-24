package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;

@AutoConfigureMockMvc
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public abstract class BaseIT {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withUsername("user")
            .withPassword("password")
            .withDatabaseName("test");

    private static void runMigrations() throws Exception {
        Path path = new File(".").toPath().toAbsolutePath().getParent().getParent()
                .resolve("migrations/db/changelog/");

        Connection connection = DriverManager.getConnection(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());

        Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));

        Liquibase liquibase = new Liquibase(
                "db-changelog.yaml",
                new DirectoryResourceAccessor(path),
                database);

        liquibase.update(new Contexts(), new LabelExpression());
    }

    @SneakyThrows
    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        postgres.start();
        runMigrations();
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
    }
}
