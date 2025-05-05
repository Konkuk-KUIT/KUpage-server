package com.kuit.kupage.common.auth;

import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.memberRole.MemberRole;
import com.kuit.kupage.domain.oauth.dto.DiscordInfoResponse;
import com.kuit.kupage.domain.role.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Getter
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class JwtTokenService {

    @Value("${secret.jwt.key}")
    private String secretKey;

    @Value("${secret.jwt.access.expiration}")
    private long accessTokenExpiration;

    @Value("${secret.jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    private final static String ACCESS = "access";
    private final static String REFRESH = "refresh";
    private final static String GUEST = "guest";

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

    public GuestTokenResponse generateGuestToken(DiscordInfoResponse userInfo){
        String discordId = userInfo.getUserResponse().getId();
        final Claims claims = Jwts.claims();
        claims.put("discordId", discordId);
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

    public boolean validateToken(String token) {
        Jws<Claims> claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token);

        return claims.getBody().getExpiration().after(new Date());
    }

    public Authentication getAuthentication(String token) {
        String[] parts = token.split("\\.");
        String jwtWithOutSignature = parts[0] + "." + parts[1] + ".";

        Claims body = Jwts.parser()
                .parseClaimsJwt(jwtWithOutSignature).getBody();

        Long memberId = body.get("sub", Long.class);
        String role = body.get("role", String.class);

        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(AuthRole.valueOf(role).getRole());

        AuthMember authMember = new AuthMember(memberId, authorities);

        return new UsernamePasswordAuthenticationToken(authMember, "", authorities);

    }

    private AuthRole getHighestPriorityAuthRole(List<Role> roles) {
        if (roles.isEmpty()) {
            return AuthRole.DEFAULT;
        }
        if (roles.stream().anyMatch(role -> role.getAuthRole().equals(AuthRole.ADMIN))) {
            return AuthRole.ADMIN;
        }
        if (roles.stream().anyMatch(role -> role.getAuthRole().equals(AuthRole.TUTOR))) {
            return AuthRole.TUTOR;
        }
        return AuthRole.MEMBER;
    }
}
