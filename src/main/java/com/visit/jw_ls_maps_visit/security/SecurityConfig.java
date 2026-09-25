package com.visit.jw_ls_maps_visit.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;


@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain springSecurityWebFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .cors(cors -> cors.configurationSource(request -> {

                CorsConfiguration configuration =
                        new CorsConfiguration();

                configuration.setAllowedOrigins(
                        List.of("*")
                );

                configuration.setAllowedMethods(
                        List.of("*")
                );

                configuration.setAllowedHeaders(
                        List.of("*")
                );

                return configuration;
            }))

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                    "/api/auth/**"
                ).permitAll()

                .requestMatchers(
                    "/api/public/**",
                    "/uploads/**",
                    "/actuator/health",
                    "/error"
                ).permitAll()

                // Recursos globais sem vínculo com circuito permanecem exclusivos do administrador geral.
                .requestMatchers("/api/contact-messages/**", "/api/decisoes-duplicadas/**")
                    .hasAnyRole("admin", "publisher", "helper", "elder")
                .requestMatchers(HttpMethod.POST, "/api/population-overrides/**", "/api/creator-contact/**", "/api/app-manuals/**", "/api/languages/**")
                    .hasAnyRole("admin", "publisher", "helper", "elder")
                .requestMatchers(HttpMethod.PUT, "/api/population-overrides/**", "/api/creator-contact/**", "/api/app-manuals/**", "/api/languages/**")
                    .hasAnyRole("admin", "publisher", "helper", "elder")
                .requestMatchers(HttpMethod.DELETE, "/api/population-overrides/**", "/api/creator-contact/**", "/api/app-manuals/**", "/api/languages/**")
                    .hasAnyRole("admin", "publisher", "helper", "elder")
                // Operações administrativas de circuito exigem um dos perfis de gestão.
                .requestMatchers("/api/admin/**").hasAnyRole("admin", "superintendente")

                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
