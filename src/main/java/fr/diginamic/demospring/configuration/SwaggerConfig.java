package fr.diginamic.demospring.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger configuration.
 *
 * <p>Provides the API metadata (title, version, description) shown at the top of
 * the generated documentation. The interactive UI is served by springdoc at
 * {@code /swagger-ui.html} and the raw document at {@code /v3/api-docs}.</p>
 */
@Configuration
public class SwaggerConfig {

    /**
     * Declares the single {@link OpenAPI} description bean picked up by springdoc.
     *
     * @return the API metadata
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("City & Department API")
                        .version("1.0")
                        .description("This API provides data about cities and departments."));
    }
}
