package com.kuit.kupage.common.auth;

import com.kuit.kupage.common.constant.ConstantProperties;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.memberRole.MemberRole;
import com.kuit.kupage.domain.role.Role;
import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@Getter
@Slf4j
@Transactional(readOnly = true)
@Service
public class JwtTokenService {

    private final byte[] secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    private final static String ACCESS = "access";
    private final static String REFRESH = "refresh";
    private final static String GUEST = "guest";

    public JwtTokenService(
            @Value("${secret.jwt.key}") String secretKey,
            @Value("${secret.jwt.access.expiration}") long accessTokenExpiration,
            @Value("${secret.jwt.refresh.expiration}") long refreshTokenExpiration) {

        this.secretKey = Base64.getDecoder().decode(secretKey);
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public AuthTokenResponse generateTokens(Member member) {
        log.info("[generateTokens] 토큰을 발급할 회원 id = {}", member.getId());
        final Claims claims = Jwts.claims();
        claims.put("sub", member.getId());

        List<MemberRole> memberRoles = member.getMemberRoles();
        List<Role> roles = memberRoles.stream().map(MemberRole::getRole).toList();

        AuthRole highestPriorityAuthRole = getHighestPriorityAuthRole(roles);
        claims.put("role", highestPriorityAuthRole.getValue());

        String accessToken = generateToken(claims, accessTokenExpiration, ACCESS);
        String refreshToken = generateToken(claims, refreshTokenExpiration, REFRESH);
        log.info("[generateTokens] 발급한 토큰 정보 access token = {}, refresh token = {}", accessToken, refreshToken);
        return new AuthTokenResponse(accessToken, refreshToken);
    }

    public GuestTokenResponse generateGuestToken(Long memberId) {
        final Claims claims = Jwts.claims();
        claims.put("sub", memberId);
        claims.put("role", "GUEST");
        String guestToken = generateToken(claims, accessTokenExpiration, GUEST);
        return new GuestTokenResponse(guestToken);
    }

    private String generateToken(Claims claims, long expiration, String type) {
        claims.put("type", type);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Authentication validateToken(String token) {
        if (token == null) {
            return null;
        }

        Jws<Claims> claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token);

        Claims body = claims.getBody();

        if (!body.getExpiration().after(new Date())) {
            throw new ExpiredJwtException(claims.getHeader(), body, "만료된 토큰입니다.");
        }

        return getAuthentication(body);
    }

    // 토큰 검증 이후에 사용
    private Authentication getAuthentication(Claims claims) {

        Long memberId = claims.get("sub", Long.class);
        String role = claims.get("role", String.class);

        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(AuthRole.valueOf(role).getRole());

        AuthMember authMember = new AuthMember(memberId, authorities);

        return new UsernamePasswordAuthenticationToken(authMember, "", authorities);
    }

    private AuthRole getHighestPriorityAuthRole(List<Role> roles) {
        if (roles.isEmpty()) {
            return AuthRole.DEFAULT;
        }
        if (roles.stream().anyMatch(role -> getAuthRole(role).isAdmin())) {
            return AuthRole.ADMIN;
        }
        if (roles.stream().anyMatch(role -> getAuthRole(role).isTutor())) {
            return AuthRole.TUTOR;
        }
        return AuthRole.MEMBER;
    }

    public AuthRole getAuthRole(Role role) {
        String roleName = role.getName().toLowerCase();
        if (roleName.contains("운영진") ||
                roleName.contains("queens") ||
                roleName.contains("presidents") ||
                roleName.contains("chairman") ||
                roleName.contains("Trinity") ||
                roleName.contains("파트장") ||
                roleName.contains("회장") ||
                roleName.contains("부회장") ||
                roleName.contains("강의자")) {
            return AuthRole.ADMIN;
        } else if (roleName.contains("스터디리더") ||
                roleName.contains("튜터")) {
            return AuthRole.TUTOR;
        } else {
            return AuthRole.MEMBER;
        }
    }
}
