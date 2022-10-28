package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.dto.jwt.TokenDto;
import com.yeoreodigm.server.dto.jwt.TokenMemberInfoDto;
import com.yeoreodigm.server.dto.member.LoginRequestDto;
import com.yeoreodigm.server.dto.member.MemberInfoDto;
import com.yeoreodigm.server.dto.member.MemberJoinRequestDto;
import com.yeoreodigm.server.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

import static com.yeoreodigm.server.dto.constraint.MemberConst.ANONYMOUS_USER;

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
    public TokenMemberInfoDto login(@RequestBody @Valid LoginRequestDto requestDto) {
        return memberService.loginV2(requestDto);
    }

    @PostMapping("/reissue")
    public TokenMemberInfoDto reissue(@RequestBody @Valid TokenDto requestDto) {
        return memberService.reissue(requestDto);
    }

    @PostMapping("/auto-login")
    public MemberInfoDto autoLogin(Authentication authentication) {
        if (Objects.isNull(authentication) || Objects.equals(ANONYMOUS_USER, authentication.getName()))
            return null;

        return new MemberInfoDto(memberService.getMemberByAuth(authentication));
    }

    @PostMapping("/logout")
    public void logout(Authentication authentication) {
        if (Objects.nonNull(authentication) && !Objects.equals(ANONYMOUS_USER, authentication.getName())) {
            memberService.logout(authentication.getName());
        }

        SecurityContextHolder.clearContext();
    }

}
