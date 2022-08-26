package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Authority;
import com.yeoreodigm.server.dto.*;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.ConfirmMemberDto;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.dto.member.MemberJoinRequestDto;
import com.yeoreodigm.server.dto.member.MemberLoginRequestDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.service.EmailService;
import com.yeoreodigm.server.service.MemberService;
import com.yeoreodigm.server.service.SurveyService;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

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

        //세션에 회원 정보 저장
        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute(SessionConst.LOGIN_MEMBER, member);

        LoginResponseDto responseDto = new LoginResponseDto(member);

        if (member.getAuthority() == Authority.ROLE_SURVEY) {
            responseDto.setSurveyIndex(surveyService.getProgress(member));
        }

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

    @ApiOperation(value = "이메일 중복 확인")
    @Tag(name = "auth")
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)"),
            @ApiResponse(code = 400, message = "(중복시) 이미 등록된 이메일입니다.")
    })
    @PostMapping("/check/email")
    public void checkEmail(@RequestBody @Valid EmailRequestDto requestDto) {
        memberService.checkDuplicateEmail(requestDto.getEmail());
    }

    @ApiOperation(value = "닉네임 중복 확인")
    @Tag(name = "auth")
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)"),
            @ApiResponse(code = 400, message = "(중복시) 이미 등록된 닉네임입니다.")
    })
    @PostMapping("/check/nickname")
    public void checkNickname(@RequestBody @Valid CheckNicknameRequestDto requestDto) {
        memberService.checkDuplicateNickname(requestDto.getNickname());
    }

    @ApiOperation(value = "이메일 인증 코드 전송")
    @Tag(name = "auth")
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)"),
            @ApiResponse(code = 400, message = "등록된 회원 정보가 없습니다.|이메일 전송에 실패하였습니다.")
    })
    @PostMapping("/email/confirm/submit")
    public void emailConfirmSubmit(@RequestBody @Valid EmailRequestDto requestDto,
                                   HttpServletRequest httpServletRequest) {
        String confirmCode = emailService.sendConfirmMail(requestDto.getEmail());

        ConfirmMemberDto confirmMemberDto = new ConfirmMemberDto(requestDto.getEmail(), confirmCode);

        //세션에 인증 요청 회원 정보 저장
        HttpSession session = httpServletRequest.getSession(true);
        session.setMaxInactiveInterval(10 * 60);
        session.setAttribute(SessionConst.CONFIRM_MEMBER, confirmMemberDto);
    }

    @ApiOperation(value = "이메일 인증 코드 확인")
    @Tag(name = "auth")
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)"),
            @ApiResponse(code = 400, message = "인증 코드의 유효 시간이 초과되었습니다.|인증 코드가 일치하지 않습니다.")
    })
    @PostMapping("/email/confirm")
    public void emailConfirm(@RequestBody @Valid ConfirmCodeRequestDto requestDto,
         @SessionAttribute(name = SessionConst.CONFIRM_MEMBER, required = false) ConfirmMemberDto confirmMemberDto) {
        if (confirmMemberDto != null) {
            emailService.checkConfirmMail(confirmMemberDto, requestDto.getConfirmCode());
        } else {
            throw new BadRequestException("인증 코드의 유효 시간이 초과되었습니다.");
        }
    }

    @ApiOperation(value = "임시 비밀번호 전송")
    @Tag(name = "auth")
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)"),
            @ApiResponse(code = 400, message = "등록된 회원 정보가 없습니다.|인증 메일 전송을 실패하였습니다.")
    })
    @PostMapping("/password/reset")
    public void passwordReset(@RequestBody @Valid EmailRequestDto requestDto) {
        emailService.sendResetMail(
                requestDto.getEmail(), memberService.resetPassword(requestDto.getEmail()));
    }

}
