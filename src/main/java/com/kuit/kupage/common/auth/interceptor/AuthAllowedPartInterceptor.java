package com.kuit.kupage.common.auth.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuit.kupage.common.auth.AllowedParts;
import com.kuit.kupage.common.auth.AuthMember;
import com.kuit.kupage.common.constant.ConstantProperties;
import com.kuit.kupage.domain.memberRole.service.MemberRoleService;
import com.kuit.kupage.domain.role.Role;
import com.kuit.kupage.domain.teamMatch.Part;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.kuit.kupage.common.response.ResponseCode.FORBIDDEN;

@Component
@Slf4j
@Data
public class AuthAllowedPartInterceptor implements HandlerInterceptor {

    private final MemberRoleService memberRoleService;
    private final ConstantProperties constantProperties;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        AuthMember authMember = (AuthMember) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authMember.isAdmin()) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        AllowedParts allowedParts = handlerMethod.getMethodAnnotation(AllowedParts.class);
        if (allowedParts == null) {
            // 메서드에 없으면 클래스 레벨에서 시도
            allowedParts = handlerMethod.getBeanType().getAnnotation(AllowedParts.class);
        }

        // 어노테이션이 없으면 인가 검사하지 않음
        if (allowedParts == null) {
            return true;
        }

        List<Part> memberParts = getCurrentBatchPart(authMember);

        boolean allowed = !Collections.disjoint(memberParts, Arrays.asList(allowedParts.value()));

        if (!allowed) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            new ObjectMapper().writeValue(response.getOutputStream(), FORBIDDEN);
            return false;
        }

        return true;
    }

    private List<Part> getCurrentBatchPart(AuthMember authMember) {
        String batchDescription = constantProperties.getCurrentBatch().getDescription();
        return memberRoleService.getMemberRolesByMemberId(authMember.getId()).stream()
                .filter(mr -> mr.getRole().getName().contains(batchDescription))
                .map(mr -> {
                    Role role = mr.getRole();
                    String[] split = role.getName().split(" ");
                    return Part.valueOf(split[1]);
                })
                .toList();
    }

}