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
        if (request.getServletPath().startsWith("/api/auth")) {
            if (request.getServletPath().equals("/api/auth/logout"))
                setAuthentication(resolveToken(request));
            filterChain.doFilter(request, response);
        } else {
            String token = resolveToken(request);

            if (StringUtils.hasText(token)) {
                int flag = tokenProvider.validateToken(token);

                if (Objects.equals(1, flag)) {
                    setAuthentication(token);
                    filterChain.doFilter(request, response);
                } else if (Objects.equals(2, flag)) {
                    //엑세스 토큰 만료
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter out = response.getWriter();
                    out.println("{\"error\": \"ACCESS_TOKEN_EXPIRED\", \"message\" : \"다시 로그인해주시기 바랍니다.\"}");
                } else {
                    //잘못된 토큰 값
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter out = response.getWriter();
                    out.println("{\"error\": \"ACCESS_TOKEN_EXPIRED\", \"message\" : \"로그인할 수 없습니다.\"}");
                }
            } else {
                //빈 토큰 값
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.println("{\"error\": \"ACCESS_TOKEN_EXPIRED\", \"message\" : \"로그인이 필요합니다.\"}");
            }
        }
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
