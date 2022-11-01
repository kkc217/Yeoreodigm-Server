package com.yeoreodigm.server.jwt;

import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CustomEmailPasswordAuthProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;

    private final CustomUserDetailsService customUserDetailsService;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    protected void additionalAuthenticationChecks(
            UserDetails userDetails, CustomEmailPasswordAuthToken authentication) {
        if (Objects.isNull(authentication.getCredentials())) {
            //토큰 인증 실패
            throw new BadRequestException("로그인할 수 없습니다.");
        }

        String password = authentication.getCredentials().toString();

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadRequestException("아이디 또는 비밀번호가 틀렸습니다.");
        }
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        UserDetails user = retrieveUser(authentication.getName());

        CustomEmailPasswordAuthToken result = new CustomEmailPasswordAuthToken(
                user,
                authentication.getCredentials(),
                authoritiesMapper.mapAuthorities(user.getAuthorities()));
        additionalAuthenticationChecks(user, result);
        result.setDetails(authentication.getDetails());

        return result;
    }

    protected final UserDetails retrieveUser(String username) {
        try {
            UserDetails loadedUser = customUserDetailsService.loadUserByUsername(username);

            if (Objects.isNull(loadedUser)) {
                throw new InternalAuthenticationServiceException(
                        "UserDetailsService returned null, which is an interface contract violation");
            }
            return loadedUser;
        } catch (Exception e) {
            throw new BadRequestException("이메일 또는 비밀번호를 잘못 입력했습니다.");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return Objects.equals(CustomEmailPasswordAuthToken.class, authentication);
    }

}
