package com.kuit.kupage.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuit.kupage.common.auth.JwtTokenService;
import com.kuit.kupage.common.response.BaseResponse;
import com.kuit.kupage.common.response.ResponseCode;
import io.jsonwebtoken.*;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (AccessDeniedException e) {
            String token = resolveToken(request);
            String rolePayload = getRolePayload(token);
            if (rolePayload == null || !rolePayload.equals("DEFAULT")) {
                throw e;
            }
            setResponse(response, ResponseCode.ROLE_REQUIRE);
        } catch (ExpiredJwtException e) {
            setResponse(response, ResponseCode.EXPIRED_ACCESS_TOKEN);
        } catch (UnsupportedJwtException e) {
            setResponse(response, ResponseCode.UNSUPPORTED_TOKEN_TYPE);
        } catch (SignatureException e) {
            setResponse(response, ResponseCode.INVALID_SIGNATURE_JWT);
        } catch (MalformedJwtException e) {
            setResponse(response, ResponseCode.MALFORMED_TOKEN_TYPE);
        } catch (IllegalArgumentException e) {
            setResponse(response, ResponseCode.INVALID_TOKEN_TYPE);
        }
    }

    private String getRolePayload(String token) {
        if (token != null) {
            String[] parts = token.split("\\.");
            String jwtWithOutSignature = parts[0] + "." + parts[1] + ".";

            Claims body = Jwts.parser()
                    .parseClaimsJwt(jwtWithOutSignature).getBody();

            return body.get("role", String.class);
        }
        return null;
    }

    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring("Bearer ".length());
        }

        return null;
    }

    private void setResponse(HttpServletResponse response, ResponseCode responseCode) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        objectMapper.writeValue(response.getWriter(), new BaseResponse<>(responseCode));
    }

}
