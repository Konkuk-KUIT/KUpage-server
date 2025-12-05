package com.kuit.kupage.common.auth.interceptor;

import com.kuit.kupage.common.auth.AuthMember;
import com.kuit.kupage.common.constant.ConstantProperties;
import com.kuit.kupage.domain.memberRole.service.MemberRoleService;
import com.kuit.kupage.domain.role.Role;
import com.kuit.kupage.domain.teamMatch.Part;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InjectionRoleInterceptor implements HandlerInterceptor {

    private final MemberRoleService memberRoleService;
    private final ConstantProperties constantProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        AuthMember authMember = (AuthMember) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Part> currentMemberPart = getCurrentMemberParts(authMember);

        request.setAttribute("parts", new MemberParts(currentMemberPart));

        return true;
    }

    private List<Part> getCurrentMemberParts(AuthMember authMember) {
        String batchDescription = constantProperties.getCurrentBatch().getDescription();
        return memberRoleService.getMemberRolesByMemberId(authMember.getId()).stream()
                .filter(mr -> mr.getRole().getName().contains(batchDescription)
                        && (mr.getRole().getName().contains("부원")
                        || mr.getRole().getName().contains("스터디장")
                        || mr.getRole().getName().contains("튜터")))
                .map(mr -> {
                    Role role = mr.getRole();
                    String[] split = role.getName().split(" ");
                    return Part.valueOf(split[1]);
                }).toList();
    }

}
