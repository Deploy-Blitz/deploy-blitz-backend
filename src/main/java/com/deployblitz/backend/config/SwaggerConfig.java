package com.deployblitz.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static java.lang.StringTemplate.STR;

@Slf4j
@Configuration
public class SwaggerConfig {
    @Value("${project.version}")
    private String version;

    @Value("${server.port}")
    private String port;

    @Bean
    public OpenAPI customOpenAPI() {
        var contact = new Contact();
        contact.setEmail("luigivis98@gmail.com");
        contact.setName("Deploy Blitz");
        contact.setUrl("https://www.github.com/Deploy-blitz");

        var mitLicense = new License().name("MIT License").url("https://github.com/Deploy-Blitz/deploy-blitz-documentation/blob/main/LICENSE");

        var components = new Components();
        components.setSchemas(schemas());

        Info info = new Info()
                .title("Deploy Blitz Documentation")
                .version(version)
                .contact(contact)
                .description("""
                        DeployBlitz automates Java-based deployments using Spring Boot, integrating seamlessly with GitHub and GitLab webhooks.\s
                        It provides robust features for secure and scalable deployment processes.
                       \s""")
                .license(mitLicense);

        return new OpenAPI().info(info);
    }

//    @Bean
//    public OpenApiCustomizer genericResponseCustomizer() {
//        return openApi -> {
//            openApi.getPaths().forEach((path, pathItem) -> {
//                log.info("Path: {}", path);
//                pathItem.readOperations().forEach(operation -> {
//                    operation.getResponses().addApiResponse("200", new ApiResponse()
//                            .description("OK")
//                            .content(new Content().addMediaType("application/json",
//                                    new MediaType().schema(genericResponseSchema()))));
//                });
//            });
//        };
//    }

    private static Map<String, Schema> schemas() {
        var schemas = new HashMap<String, Schema>();
        schemas.put("GenericResponse", genericResponseSchema());
        return schemas;
    }

    private static Schema<?> genericResponseSchema() {
        return new Schema<>()
                .type("object")
                .addProperty("code", new Schema<>().type("integer").format("int32").example(200))
                .addProperty("message", new Schema<>().type("string").example("OK"));
    }
}
