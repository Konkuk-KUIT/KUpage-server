package com.kuit.kupage.common.swagger;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SwaggerErrorResponses {
    SwaggerErrorResponse value() default SwaggerErrorResponse.DEFAULT;
}