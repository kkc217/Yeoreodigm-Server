package com.yeoreodigm.server.config;

import com.yeoreodigm.server.jwt.JwtAccessDeniedHandler;
import com.yeoreodigm.server.jwt.JwtAuthenticationEntryPoint;
import com.yeoreodigm.server.jwt.JwtFilter;
import com.yeoreodigm.server.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.http.HttpMethod.*;

@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig {

    private final TokenProvider tokenProvider;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors()

                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                .and()
                .formLogin().disable();

        http.httpBasic().disable()
                .authorizeRequests()
                .mvcMatchers("/api/auth/**").permitAll()

                .mvcMatchers("/api/member/auth").hasAnyAuthority("ROLE_NOT_PERMITTED", "ROLE_ADMIN")
                .mvcMatchers(PATCH, "/api/member/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .mvcMatchers(DELETE, "/api/member").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .mvcMatchers(DELETE, "/api/member/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .mvcMatchers(POST, "/api/member/password").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .mvcMatchers(POST, "/api/member/**").permitAll()
                .mvcMatchers(PUT, "/api/member/**").permitAll()
                .mvcMatchers(GET, "/api/member").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_SURVEY")
                .mvcMatchers(GET, "/api/member/profile").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .mvcMatchers(GET, "/api/member/follower/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .mvcMatchers(GET, "/api/member/followee/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .mvcMatchers(GET, "/api/member/**").permitAll()

                .mvcMatchers("/api/survey/**").hasAnyAuthority("ROLE_SURVEY", "ROLE_ADMIN")

                .mvcMatchers(GET, "/api/search").permitAll()
                .mvcMatchers(GET, "/api/search/**").permitAll()

                .mvcMatchers(GET, "/api/place/detail/**").permitAll()
                .mvcMatchers( "/api/place/detail/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                .mvcMatchers(GET, "/api/place/**").permitAll()
                .mvcMatchers("/api/place/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                .mvcMatchers(GET, "/api/note/detail/**").permitAll()
                .mvcMatchers("/api/note/detail/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                .mvcMatchers(GET, "/api/note/info/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .mvcMatchers(GET, "/api/note/companion/{travelNoteId}").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .mvcMatchers(GET, "/api/note/comment/{travelNoteId}/{day}").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .mvcMatchers(GET, "/api/note/my/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .mvcMatchers(GET, "/api/note/board/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .mvcMatchers(GET, "/api/note/**").permitAll()
                .mvcMatchers("/api/note/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                .mvcMatchers(GET, "/api/recommend/**").permitAll()

                .mvcMatchers(GET, "/api/course/**").permitAll()
                .mvcMatchers("/api/course").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .mvcMatchers("/api/course/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                .mvcMatchers(GET, "api/photodigm/picture/{photodigmId}").permitAll()
                .mvcMatchers(GET, "api/photodigm/{page}/{limit}").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .mvcMatchers("/api/photodigm/**").permitAll()

                .mvcMatchers(GET,"/api/board/detail/**").permitAll()
                .mvcMatchers("/api/board/detail/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                .mvcMatchers(GET, "/api/board/modification/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .mvcMatchers(GET, "/api/board").permitAll()
                .mvcMatchers(GET, "/api/board/**").permitAll()
                .mvcMatchers("/api/board").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .mvcMatchers("/api/board/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                .mvcMatchers("/api/**").authenticated()
                .anyRequest().denyAll()

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

//        configuration.addAllowedOrigin("https://yeoreodigm.com");
//        configuration.addAllowedOrigin("https://www.yeoreodigm.com");
//        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedMethod(GET);
        configuration.addAllowedMethod(POST);
        configuration.addAllowedMethod(PUT);
        configuration.addAllowedMethod(PATCH);
        configuration.addAllowedMethod(DELETE);
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
