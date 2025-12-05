package com.kuit.kupage.unit.common.config;


import com.kuit.kupage.common.auth.PartsArgumentResolver;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@TestConfiguration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PartsArgumentResolver());
    }
}
