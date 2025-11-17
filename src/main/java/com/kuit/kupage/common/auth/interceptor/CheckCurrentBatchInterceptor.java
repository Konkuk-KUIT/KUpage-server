package com.kuit.kupage.common.auth.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuit.kupage.common.auth.AuthMember;
import com.kuit.kupage.common.constant.ConstantProperties;
import com.kuit.kupage.domain.memberRole.service.MemberRoleService;
import com.kuit.kupage.domain.role.Role;
import com.kuit.kupage.domain.teamMatch.Part;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

import static com.kuit.kupage.common.response.ResponseCode.NOT_CURRENT_BATCH_MEMBER;

@RequiredArgsConstructor
@Component
@Slf4j
public class CheckCurrentBatchInterceptor implements HandlerInterceptor {

    private final MemberRoleService memberRoleService;
    private final ConstantProperties constantProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("AuthPmInterceptor 진입");


        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        AuthMember authMember = (AuthMember) authentication.getPrincipal();

        boolean isCurrentBatch = memberRoleService.isCurrentBatch(authMember.getId());

        if (!isCurrentBatch) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            new ObjectMapper().writeValue(response.getOutputStream(), NOT_CURRENT_BATCH_MEMBER);
        }

        return isCurrentBatch;
    }

}
