package ru.senla.socialnetwork.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ProblemDetail;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Social Network Api",
        description = "API социальной сети",
        version = "1.0.0",
        contact = @Contact(name = "Nemkov Daniil", url = "https://github.com/nemk0ff")
    )
)
public class SwaggerConfig {

  @SuppressWarnings("unchecked")
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .components(new Components()
            .addSecuritySchemes("bearerAuth",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
            )
            .addSchemas("ProblemDetail", new Schema<ProblemDetail>()
                .type("object")
                .properties(
                    Map.of(
                        "title", new StringSchema().description("Тип ошибки"),
                        "status", new IntegerSchema().description("HTTP статус код"),
                        "detail", new StringSchema().description("Детальное описание ошибки"),
                        "timestamp", new StringSchema().format("date-time").description("Время возникновения ошибки"),
                        "path", new StringSchema().description("Путь запроса")
                    )
                )
            )
        )
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
  }
}