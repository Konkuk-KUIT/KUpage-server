package com.kuit.kupage.common.config;

import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.common.swagger.ExampleHolder;
import com.kuit.kupage.common.swagger.SwaggerErrorResponse;
import com.kuit.kupage.common.swagger.SwaggerErrorResponses;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.HandlerMethod;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;

@OpenAPIDefinition(
        servers = @Server(url = "/", description = "default server url"),
        info = @Info(title = "👑 KUITee API 명세서",
                description = "springdoc을 이용한 swagger API 문서입니다 : https://springdoc.org/",
                contact = @Contact(name = "KUIT", url = "https://github.com/Konkuk-KUIT", email = "https://www.konkuk-kuit.com/"),
                version = "1.0"
        )
)

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().components(new Components().addSecuritySchemes(
                "JWT", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .description("jwt토큰")
                        .in(SecurityScheme.In.HEADER)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .name(HttpHeaders.AUTHORIZATION)
        ));
    }

    @Bean
    public OperationCustomizer customize() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            SwaggerErrorResponses errorAnn =
                    AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), SwaggerErrorResponses.class);
            if (errorAnn == null) {
                errorAnn = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), SwaggerErrorResponses.class);
            }
            if (errorAnn != null) {
                generateErrorCodeResponseExample(operation, errorAnn.value());
            }
            return operation;
        };
    }

    public SecurityScheme bearerAuth() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name(HttpHeaders.AUTHORIZATION);
    }


    private void generateErrorCodeResponseExample(Operation operation, SwaggerErrorResponse type) {

        ApiResponses responses = operation.getResponses();
        Set<ResponseCode> responseCodeSet = type.getResponseCodeSet();

        Map<Integer, List<ExampleHolder>> statusWithExampleHolders =
                responseCodeSet.stream()
                        .map(
                                responseCode -> {
                                    return ExampleHolder.builder()
                                            .holder(
                                                    getSwaggerExample(responseCode))
                                            .code(responseCode.getCode())
                                            .name(responseCode.toString())
                                            .build();
                                }
                        ).collect(groupingBy(ExampleHolder::getCode));
        addExamplesToResponses(responses, statusWithExampleHolders);
    }


    private Example getSwaggerExample(ResponseCode responseCode) {
        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("success", responseCode.isSuccess());
        responseMap.put("code", responseCode.getCode());
        responseMap.put("message", responseCode.getMessage());
        responseMap.put("result", null); // null 안전하게 허용됨

        Example example = new Example();
        example.description(responseCode.getMessage());
        example.setValue(responseMap);

        return example;
    }


    private void addExamplesToResponses(
            ApiResponses responses, Map<Integer, List<ExampleHolder>> statusWithExampleHolders) {
        statusWithExampleHolders.forEach(
                (status, v) -> {
                    Content content = new Content();
                    MediaType mediaType = new MediaType();
                    ApiResponse apiResponse = new ApiResponse();
                    v.forEach(
                            exampleHolder -> {
                                mediaType.addExamples(
                                        exampleHolder.getName(), exampleHolder.getHolder());
                            });
                    content.addMediaType("application/json", mediaType);
                    apiResponse.setDescription("");
                    apiResponse.setContent(content);
                    responses.addApiResponse(status.toString(), apiResponse);
                });
    }
}
