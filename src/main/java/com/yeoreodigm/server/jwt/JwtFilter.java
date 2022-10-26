package com.yeoreodigm.server.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

import static com.yeoreodigm.server.dto.constraint.JWTConst.AUTHORIZATION_HEADER;
import static com.yeoreodigm.server.dto.constraint.JWTConst.BEARER_TYPE;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws IOException, ServletException {
        String token = resolveToken(request);

        if (Objects.nonNull(token) && !request.getServletPath().startsWith("/api/auth")) {
            int flag = tokenProvider.validateToken(token);
            if (Objects.equals(1, flag)) {
                setAuthentication(token);
            } else if (Objects.equals(2, flag)) {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.println("{\"error\": \"401\", \"message\" : \"토큰이 만료되었습니다.\"}");
                return;
            } else if (Objects.equals(3, flag)) {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.println("{\"error\": \"403\", \"message\" : \"다시 로그인해주시기 바랍니다.\"}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String token) {
        Authentication authentication = tokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
