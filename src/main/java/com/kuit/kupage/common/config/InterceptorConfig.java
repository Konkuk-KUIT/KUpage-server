package com.kuit.kupage.common.config;

import com.kuit.kupage.common.auth.interceptor.AuthAllowedPartInterceptor;
import com.kuit.kupage.common.auth.interceptor.CheckCurrentBatchInterceptor;
import com.kuit.kupage.common.auth.interceptor.InjectionRoleInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    private final CheckCurrentBatchInterceptor checkCurrentBatchInterceptor;
    private final AuthAllowedPartInterceptor authAllowedPartInterceptor;
    private final InjectionRoleInterceptor injectionRoleInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(checkCurrentBatchInterceptor)
                .order(1)
                .addPathPatterns("/teams/applications", "/teams/{teamId}/match", "/teams");

        registry.addInterceptor(authAllowedPartInterceptor)
                .order(2)
                .addPathPatterns("/teams/{teamId}/applications");

        registry.addInterceptor(injectionRoleInterceptor)
                .order(3)
                .addPathPatterns("/teams/applications");
    }
}
