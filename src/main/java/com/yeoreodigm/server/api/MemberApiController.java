package com.yeoreodigm.server.api;

import com.yeoreodigm.server.domain.Gender;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/auth/join")
    public void saveMember(@RequestBody @Valid SaveMemberRequest request) {
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

    @Data
    static class SaveMemberRequest {
        @NotEmpty(message = "이메일은 필수입니다.")
        private String email;

        @NotEmpty(message = "닉네임은 필수입니다.")
        private String nickname;

        @NotEmpty(message = "비밀번호는 필수입니다.")
        private String password;

        private Gender gender;

        private int year;
        private int month;
        private int day;

        private boolean optional;
    }

}
