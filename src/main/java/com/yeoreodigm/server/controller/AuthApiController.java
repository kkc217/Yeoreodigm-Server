package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.dto.jwt.TokenDto;
import com.yeoreodigm.server.dto.member.MemberLoginRequestDto;
import com.yeoreodigm.server.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthService authService;

    @PostMapping("login")
    public TokenDto login(@RequestBody @Valid MemberLoginRequestDto requestDto) {
        return authService.login(requestDto);
    }

    @PostMapping("/reissue")
    public TokenDto reissue(@RequestBody @Valid TokenDto requestDto) {
        return authService.reissue(requestDto);
    }

}
