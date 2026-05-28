package br.tcc.pega.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configura o Swagger UI com suporte a Bearer Token JWT.
 * Acesse: http://localhost:8080/swagger-ui.html
 */
@Configuration
@OpenAPIDefinition(info = @Info(
    title = "PEGA API",
    version = "1.0",
    description = "Plataforma Gamificada de Apoio à Alfabetização — TCC 2024/2025"
))
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApiConfig {
}
