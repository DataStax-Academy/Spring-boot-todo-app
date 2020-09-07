package com.datastax.examples.conf;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiDocumentationConfig {

    @Bean
    public OpenAPI openApiSpec(@Value("1.3.9") String appVersion) {
        String des = "Implementation of TodoBackend application with Spring WebMVC and storage in Apache Cassandra";
        Info info  = new Info().title("Developer Workshop :: Build a Spring Java Microservice with Apache Cassandra")
                .version(appVersion).description(des)
                .termsOfService("http://swagger.io/terms/")
                .license(new License().name("Apache 2.0")
                        .url("http://springdoc.org"));
        return new OpenAPI().addServersItem(new Server().url("/")).info(info);
    }

    @Bean
    public GroupedOpenApi actuatorApi() {
        return GroupedOpenApi.builder().setGroup("Monitoring (Actuator)")
                .pathsToMatch("/actuator/**")
                .pathsToExclude("/actuator/health/*")
                .build();
    }

}
