package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Authority;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.member.MemberAuthDto;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.dto.member.LoginResponseDto;
import com.yeoreodigm.server.dto.member.MemberJoinRequestDto;
import com.yeoreodigm.server.dto.member.MemberLoginRequestDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.service.EmailService;
import com.yeoreodigm.server.service.MemberService;
import com.yeoreodigm.server.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {

    private final MemberService memberService;

    private final SurveyService surveyService;

    private final EmailService emailService;

    @PostMapping("/new")
    public void join(@RequestBody @Valid MemberJoinRequestDto requestDto) {
        memberService.join(requestDto);
    }

    @PostMapping("/login")
    public LoginResponseDto login(
            @RequestBody @Valid MemberLoginRequestDto requestDto,
            HttpServletRequest httpServletRequest) {
        //회원 유무, 비밀번호 일치 확인
        Member member = memberService.login(requestDto.getEmail(), requestDto.getPassword());

        LoginResponseDto responseDto = new LoginResponseDto(member);

        if (member.getAuthority() == Authority.ROLE_NOT_PERMITTED) {
            return responseDto;
        } else if (member.getAuthority() == Authority.ROLE_SURVEY) {
            responseDto.setSurveyIndex(surveyService.getProgress(member));
        }

        //세션에 회원 정보 저장
        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute(SessionConst.LOGIN_MEMBER, member);

        return responseDto;
    }

    @GetMapping("/auto-login")
    public LoginResponseDto autoLogin(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        if (member != null) {
            LoginResponseDto responseDto = new LoginResponseDto(member);

            if (member.getAuthority() == Authority.ROLE_SURVEY) {
                responseDto.setSurveyIndex(surveyService.getProgress(member));
            }

            return responseDto;
        } else {
            throw new BadRequestException("다시 로그인해주시기 바랍니다.");
        }
    }

    @PostMapping("/logout")
    public void logout(
            HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    @PostMapping("/email")
    public void checkEmail(
            @RequestBody HashMap<String, String> request) {
        memberService.checkDuplicateEmail(request.get("email"));
    }

    @PostMapping("/nickname")
    public void checkNickname(
            @RequestBody HashMap<String, String> request) {
        memberService.checkDuplicateNickname(request.get("nickname"));
    }

    @PostMapping("/auth")
    public void submitAuth(
            @RequestBody HashMap<String, String> request,
            HttpServletRequest httpServletRequest) {
        String email = request.get("email");
        String confirmCode = emailService.sendConfirmMail(email);

        MemberAuthDto memberAuthDto = new MemberAuthDto(email, confirmCode);

        //세션에 인증 요청 회원 정보 저장
        HttpSession session = httpServletRequest.getSession(true);
        session.setMaxInactiveInterval(10 * 60);
        session.setAttribute(SessionConst.CONFIRM_MEMBER, memberAuthDto);
    }

    @PatchMapping("/auth/{code}")
    public void confirmAuth(
            @PathVariable("code") String code,
            HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);

        if (session != null) {
            MemberAuthDto memberAuthDto = (MemberAuthDto) session.getAttribute(SessionConst.CONFIRM_MEMBER);
            memberService.confirmAuth(memberAuthDto, code);
            session.invalidate();
        } else {
            throw new BadRequestException("인증 코드의 유효 시간이 초과되었습니다.");
        }
    }

    @PutMapping("/password/{email}")
    public void passwordReset(
            @PathVariable("email") String email) {
        memberService.resetPassword(email);
    }

}
