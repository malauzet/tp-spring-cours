package fr.diginamic.demospring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the DemoSpring application.
 *
 * <p>Exposes a small REST API to manage French cities ({@code /cities}) and
 * departments ({@code /departments}), backed by JPA/Hibernate over a MariaDB
 * database. The OpenAPI description is available at {@code /swagger-ui.html}.</p>
 */
@SpringBootApplication
public class DemoSpringApplication {

    /**
     * Boots the Spring context and starts the embedded web server.
     *
     * @param args standard command-line arguments forwarded to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(DemoSpringApplication.class, args);
    }
}
