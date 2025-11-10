package com.kuit.kupage.common.config;

import com.kuit.kupage.common.auth.interceptor.AuthPmInterceptor;
import com.kuit.kupage.common.auth.interceptor.CheckCurrentBatchInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    private final CheckCurrentBatchInterceptor checkCurrentBatchInterceptor;
    private final AuthPmInterceptor authPmInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(checkCurrentBatchInterceptor)
                .order(1)
                .addPathPatterns("/teams/applications", "/teams/{teamId}/match", "/ideas");

        registry.addInterceptor(authPmInterceptor)
                .order(2)
                .addPathPatterns("/teams/applications", "/teams/{teamId}/applications", "/ideas");
    }
}
