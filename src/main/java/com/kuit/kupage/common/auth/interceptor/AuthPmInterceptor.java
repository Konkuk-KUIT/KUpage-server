package com.kuit.kupage.common.auth.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuit.kupage.common.auth.AuthMember;
import com.kuit.kupage.domain.memberRole.service.MemberRoleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.kuit.kupage.common.response.ResponseCode.FORBIDDEN;

@RequiredArgsConstructor
@Component
@Slf4j
public class AuthPmInterceptor implements HandlerInterceptor {

    private final MemberRoleService memberRoleService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("AuthPmInterceptor 진입");

        AuthMember authMember = (AuthMember) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authMember.isAdmin()) {
            return true;
        }

        boolean isPm = isPm(authMember);

        if (!isPm) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            new ObjectMapper().writeValue(response.getOutputStream(), FORBIDDEN);

        }

        return isPm;
    }

    private boolean isPm(AuthMember authMember) {
        return memberRoleService.getMemberRolesByMemberId(authMember.getId()).stream()
                .anyMatch(mr -> mr.getRole().getName().contains("PM"));
    }

}
