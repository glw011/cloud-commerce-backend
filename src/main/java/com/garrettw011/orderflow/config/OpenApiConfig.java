package com.garrettw011.orderflow.config;

import com.garrettw011.orderflow.common.ApiDocs;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI orderFlowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OrderFlow API")
                        .description("""
                                Order management backend: catalog, inventory customers, orders, payments and reporting. \
                                Authenticate at /api/v1/auth/login, click 'Authorize' and then paste access token to \
                                call secured endpoints.""")
                        .version("v1")
                        .contact(new Contact()
                                .name("Garrett W")
                                .url("https://github.com/glw011/cloud-commerce-backend"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org./licenses/LICENSE-2.0")))
                .components(new Components()
                        .addSecuritySchemes(ApiDocs.BEARER_SCHEME, new SecurityScheme()
                                .name(ApiDocs.BEARER_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste access token from /api/v1/auth/login (no 'Bearer' prefix).")));
    }
}


