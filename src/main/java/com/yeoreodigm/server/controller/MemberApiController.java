package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.api.CheckEmailRequestDto;
import com.yeoreodigm.server.api.SaveMemberRequestDto;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/auth/join")
    public void saveMember(@RequestBody @Valid SaveMemberRequestDto request) {
        LocalDate birth = LocalDate.of(request.getYear(), request.getMonth(), request.getDay());

        Member member = new Member(
                request.getEmail(),
                request.getPassword(),
                request.getNickname(),
                birth,
                request.getGender(),
                request.isOptional()
        );

        memberService.join(member);
    }

    @PostMapping("/api/auth/check/email")
    public void checkEmail(@RequestBody @Valid CheckEmailRequestDto request) {
        memberService.validateDuplicateEmail(request.getEmail());
    }

}
