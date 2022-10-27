package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.dto.constraint.MemberConst;
import com.yeoreodigm.server.dto.jwt.TokenDto;
import com.yeoreodigm.server.dto.jwt.TokenMemberInfoDto;
import com.yeoreodigm.server.dto.member.LoginRequestDto;
import com.yeoreodigm.server.dto.member.MemberJoinRequestDto;
import com.yeoreodigm.server.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthApiController {

    private final MemberService memberService;

    @PostMapping("/new")
    public void join(@RequestBody @Valid MemberJoinRequestDto requestDto) {
        memberService.join(requestDto);
    }

    @PostMapping("/login")
    public TokenMemberInfoDto login(
            HttpServletResponse response,
            @Value("${jwt.remember-me-cookie-expire-time}") int rememberMeExpireTime,
            @RequestBody @Valid LoginRequestDto requestDto) {
        TokenMemberInfoDto tokenMemberInfoDto = memberService.loginV2(requestDto);

        Cookie cookie;
        if (requestDto.isRememberMe()) {
            cookie = new Cookie("remember-me", tokenMemberInfoDto.getAccessToken());
            cookie.setMaxAge(rememberMeExpireTime);
        } else {
            cookie = new Cookie("remember-me", null);
            cookie.setMaxAge(0);
        }
        cookie.setPath("/");
        response.addCookie(cookie);

        return tokenMemberInfoDto;
    }

    @PostMapping("/reissue")
    public TokenMemberInfoDto reissue(@RequestBody @Valid TokenDto requestDto) {
        return memberService.reissue(requestDto);
    }

    @PostMapping("/auto-login")
    public TokenMemberInfoDto autoLogin(
            @CookieValue("remember-me") String accessToken) {
        return memberService.autoLogin(accessToken);
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response, Authentication authentication) {
        if (Objects.nonNull(authentication) && !Objects.equals(MemberConst.ANONYMOUS_USER, authentication.getName())) {
            memberService.logout(authentication.getName());
        }

        Cookie cookie = new Cookie("remember-me", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        SecurityContextHolder.clearContext();
    }

}
