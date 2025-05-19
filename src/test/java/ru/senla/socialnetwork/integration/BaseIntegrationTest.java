package ru.senla.socialnetwork.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class BaseIntegrationTest {

  @SuppressWarnings("resource")
  @Container
  protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
      "postgres:17-alpine")
      .withDatabaseName("testdb")
      .withUsername("test")
      .withPassword("test")
      .withStartupTimeout(Duration.ofSeconds(120))
      .withConnectTimeoutSeconds(120)
      .withInitScripts(
          "migrations/001-initial-schema.sql",
          "migrations/002-initial-data.sql"
      );

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @BeforeAll
  static void waitForDb() {
    postgres.waitingFor(
        Wait.forLogMessage(".*database system is ready to accept connections.*", 1)
            .withStartupTimeout(Duration.ofSeconds(120))
    );
  }
}