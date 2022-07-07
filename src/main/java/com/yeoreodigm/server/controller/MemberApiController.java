package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.dto.*;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.ConfirmMemberDto;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.service.EmailService;
import com.yeoreodigm.server.service.MemberService;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.NoSuchElementException;

@Tag(name = "auth", description = "인증에 관한 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MemberApiController {

    private final MemberService memberService;

    private final EmailService emailService;

    @ApiOperation(value = "회원 가입")
    @Tag(name = "auth")
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)"),
            @ApiResponse(code = 409, message = "(중복시) 이미 등록된 이메일입니다. / 이미 등록된 닉네임입니다.")
    })
    @PostMapping("/join")
    public void joinMember(@RequestBody @Valid JoinMemberRequestDto requestDto) {
        LocalDate birth = LocalDate.of(requestDto.getYear(), requestDto.getMonth(), requestDto.getDay());

        Member member = new Member(
                requestDto.getEmail(),
                requestDto.getPassword(),
                requestDto.getNickname(),
                birth,
                requestDto.getGender(),
                requestDto.isOptional()
        );

        memberService.join(member);
    }

    @ApiOperation(value = "로그인")
    @Tag(name = "auth")
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)"),
            @ApiResponse(code = 404, message = "등록된 이메일 정보가 없습니다."),
            @ApiResponse(code = 409, message = "비밀번호가 일치하지 않습니다.")
    })
    @PostMapping("/login")
    public LoginMemberDto login(@RequestBody @Valid LoginRequestDto requestDto,
                                HttpServletRequest httpServletRequest) {
        //회원 유무, 비밀번호 일치 확인
        Member member = memberService.checkLoginInfo(requestDto.getEmail(), requestDto.getPassword());

        LoginMemberDto loginMemberDto = new LoginMemberDto(
                member.getEmail(), member.getNickname(), member.getAuthority());

        //세션에 회원 정보 저장
        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMemberDto);

        return loginMemberDto;
    }

    @ApiOperation(value = "오토 로그인")
    @Tag(name = "auth")
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)"),
            @ApiResponse(code = 404, message = "세션이 만료되었습니다.")
    })
    @PostMapping("/autologin")
    public LoginMemberDto autoLogin(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);

        if (session != null) {
            return (LoginMemberDto)session.getAttribute(SessionConst.LOGIN_MEMBER);
        } else {
            throw new NoSuchElementException("세션이 만료되었습니다.");
        }
    }

    @ApiOperation(value = "로그아웃")
    @Tag(name = "auth")
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)")
    })
    @PostMapping("/logout")
    public void logout(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);

        if (session != null) {
            session.invalidate();
        }
    }

    @ApiOperation(value = "이메일 중복 확인")
    @Tag(name = "auth")
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)"),
            @ApiResponse(code = 409, message = "(중복시) 이미 등록된 이메일입니다.")
    })
    @PostMapping("/check/email")
    public void checkEmail(@RequestBody @Valid EmailRequestDto requestDto) {
        memberService.validateDuplicateEmail(requestDto.getEmail());
    }

    @ApiOperation(value = "닉네임 중복 확인")
    @Tag(name = "auth")
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)"),
            @ApiResponse(code = 409, message = "(중복시) 이미 등록된 닉네임입니다.")
    })
    @PostMapping("/check/nickname")
    public void checkNickname(@RequestBody @Valid CheckNicknameRequestDto requestDto) {
        memberService.validateDuplicateNickname(requestDto.getNickname());
    }

    @ApiOperation(value = "이메일 인증 코드 전송")
    @Tag(name = "auth")
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)"),
            @ApiResponse(code = 404, message = "등록된 회원 정보가 없습니다."),
            @ApiResponse(code = 409, message = "이메일 전송에 실패하였습니다.")
    })
    @PostMapping("/email/confirm/submit")
    public void emailConfirmSubmit(@RequestBody @Valid EmailRequestDto requestDto, HttpServletRequest httpServletRequest) {
        String confirmCode = emailService.genRandomCode();
        emailService.sendConfirmMail(requestDto.getEmail(), confirmCode);

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
            @ApiResponse(code = 404, message = "인증 코드의 유효 시간이 초과되었습니다."),
            @ApiResponse(code = 409, message = "인증 코드가 일치하지 않습니다.")
    })
    @PostMapping("/email/confirm")
    public void emailConfirm(@RequestBody @Valid ConfirmCodeRequestDto requestDto, HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);

        if (session != null) {
            ConfirmMemberDto confirmMemberDto = (ConfirmMemberDto) session.getAttribute(SessionConst.CONFIRM_MEMBER);
            emailService.checkConfir링mMail(confirmMemberDto, requestDto.getConfirmCode());
            session.invalidate();
        } else {
            throw new NoSuchElementException("인증 코드의 유효 시간이 초과되었습니다.");
        }
    }

    @ApiOperation(value = "임시 비밀번호 전송")
    @Tag(name = "auth")
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)"),
            @ApiResponse(code = 404, message = "등록된 회원 정보가 없습니다."),
            @ApiResponse(code = 409, message = "인증 메일 전송을 실패하였습니다.")
    })
    @PostMapping("/password/reset")
    public void passwordReset(@RequestBody @Valid EmailRequestDto requestDto) {
        emailService.sendResetMail(requestDto.getEmail());
    }

}
