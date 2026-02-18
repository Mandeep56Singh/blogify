package com.mandeep.blogify.shared.config;

import com.mandeep.blogify.auth.AuthConstants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI configOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Blogify API docs")
                        .version("1.0")
                        .description("Modular Monolith Blogging API Documentation")
                        .contact(
                                new Contact()
                                        .name("Mandeep Singh")
                                        .email("mandeepraj2312@gmail")

                        )
                        .summary("This App shows REST APIs for Blogging App")

                ).addSecurityItem(new SecurityRequirement().addList(AuthConstants.SECURITY_SCHEMA_NAME))
                .components(new Components()
                        .addSecuritySchemes(AuthConstants.SECURITY_SCHEMA_NAME,
                                new SecurityScheme()
                                        .name(AuthConstants.SECURITY_SCHEMA_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
