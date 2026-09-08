package fr.diginamic.demospring.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.httpBasic(Customizer.withDefaults());

        // GET /cities/** est public, tout le reste doit être authentifié
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/cities/**").permitAll()
                .anyRequest().authenticated()
        );

        return http.build();
    }
}