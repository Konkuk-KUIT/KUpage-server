package com.kuit.kupage.common.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

public class PartsArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 어노테이션이 붙어 있는지 확인
        boolean hasAnnotation = parameter.hasParameterAnnotation(MemberParts.class);

        // 파라미터의 타입이 MemberParts 확인 (인터셉터에서 저장한 타입과 일치해야 함)
        boolean isMemberParts = com.kuit.kupage.common.auth.interceptor.MemberParts.class.isAssignableFrom(parameter.getParameterType());

        return hasAnnotation && isMemberParts;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        Object parts = request.getAttribute("parts");
        if (parts == null) {
            return new com.kuit.kupage.common.auth.interceptor.MemberParts(List.of());
        }
        return parts;
    }
}
