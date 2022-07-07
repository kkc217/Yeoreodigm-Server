package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.api.*;
import com.yeoreodigm.server.api.constraint.SessionConst;
import com.yeoreodigm.server.api.session.ConfirmMember;
import com.yeoreodigm.server.api.session.LoginMember;
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
import java.util.Map;
import java.util.NoSuchElementException;

@Tag(name = "auth", description = "인증에 관한 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MemberApiController {

    private final MemberService memberService;

    private final EmailService emailService;

    @ApiOperation(value = "회원 가입", notes = "신규 회원 정보를 등록한다.")
    @Tag(name = "auth")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "이메일", paramType = "query", required = true),
            @ApiImplicitParam(name = "nickname", value = "닉네임", paramType = "query", required = true),
            @ApiImplicitParam(name = "password", value = "비밀번호", paramType = "query", required = true),
            @ApiImplicitParam(name = "gender", value = "성별", paramType = "query", required = true),
            @ApiImplicitParam(name = "year", value = "출생연도", paramType = "query", required = true),
            @ApiImplicitParam(name = "month", value = "출생월", paramType = "query", required = true),
            @ApiImplicitParam(name = "day", value = "출생일", paramType = "query", required = true),
            @ApiImplicitParam(name = "optional", value = "선택약관 동의 여부", paramType = "query", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)"),
            @ApiResponse(code = 409, message = "(중복시) 이미 등록된 이메일입니다. / 이미 등록된 닉네임입니다.")
    })
    @PostMapping("/join")
    public void saveMember(@RequestBody @Valid SaveMemberRequestDto requestDto) {
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

    @ApiOperation(value = "로그인", notes = "이메일과 비밀번호를 받아 로그인한다.")
    @Tag(name = "auth")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "이메일", paramType = "query", required = true),
            @ApiImplicitParam(name = "password", value = "비밀번호", paramType = "query", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)"),
            @ApiResponse(code = 404, message = "등록된 이메일 정보가 없습니다."),
            @ApiResponse(code = 409, message = "비밀번호가 일치하지 않습니다.")
    })
    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody @Valid LoginRequestDto requestDto, HttpServletRequest httpServletRequest) {
        Member member = memberService.login(requestDto.getEmail(), requestDto.getPassword());

        HttpSession session = httpServletRequest.getSession(true);

        LoginResponseDto loginResponseDto = new LoginResponseDto(member.getEmail(), member.getNickname(), member.getAuthority());
        session.setAttribute(SessionConst.LOGIN_MEMBER, new LoginMember(member.getEmail(), member.getNickname(), member.getAuthority()));
        return loginResponseDto;
    }

    @ApiOperation(value = "오토 로그인", notes = "세션 정보로 로그인한다.")
    @Tag(name = "auth")
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)"),
            @ApiResponse(code = 404, message = "세션이 만료되었습니다.")
    })
    @PostMapping("/autologin")
    public AutoLoginResponseDto autoLogin(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);

        if (session != null) {
            LoginMember loginMember = (LoginMember) session.getAttribute(SessionConst.LOGIN_MEMBER);
            return new AutoLoginResponseDto(loginMember.getEmail(), loginMember.getNickname(), loginMember.getAuthority());
        } else {
            throw new NoSuchElementException("세션이 만료되었습니다.");
        }
    }

    @ApiOperation(value = "로그아웃", notes = "세션 정보로 로그아웃한다.")
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

    @ApiOperation(value = "이메일 중복 확인", notes = "이미 등록되어 있는 이메일인지 확인한다.")
    @Tag(name = "auth")
    @ApiImplicitParam(name = "email", value = "이메일", paramType = "query", required = true)
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)"),
            @ApiResponse(code = 409, message = "(중복시) 이미 등록된 이메일입니다.")
    })
    @PostMapping("/check/email")
    public void checkEmail(@RequestBody @Valid EmailRequestDto requestDto) {
        memberService.validateDuplicateEmail(requestDto.getEmail());
    }

    @ApiOperation(value = "닉네임 중복 확인", notes = "이미 등록되어 있는 닉네임인지 확인한다.")
    @Tag(name = "auth")
    @ApiImplicitParam(name = "nickname", value = "닉네임", paramType = "query", required = true)
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)"),
            @ApiResponse(code = 409, message = "(중복시) 이미 등록된 닉네임입니다.")
    })
    @PostMapping("/check/nickname")
    public void checkNickname(@RequestBody @Valid CheckNicknameRequestDto requestDto) {
        memberService.validateDuplicateNickname(requestDto.getNickname());
    }

    @ApiOperation(value = "이메일 인증 코드 전송", notes = "요청받은 이메일로 인증 코드를 전송한다.")
    @Tag(name = "auth")
    @ApiImplicitParam(name = "email", value = "이메일", paramType = "query", required = true)
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)"),
            @ApiResponse(code = 404, message = "등록된 회원 정보가 없습니다."),
            @ApiResponse(code = 409, message = "이메일 전송에 실패하였습니다.")
    })
    @PostMapping("/email/confirm/submit")
    public void emailConfirmSubmit(@RequestBody @Valid EmailRequestDto requestDto, HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(true);
        session.setMaxInactiveInterval(10 * 60);

        String confirmCode = emailService.genRandomCode();
        emailService.sendConfirmMail(requestDto.getEmail(), confirmCode);

        ConfirmMember confirmMember = new ConfirmMember(requestDto.getEmail(), confirmCode);
        session.setAttribute(SessionConst.CONFIRM_MEMBER, confirmMember);
    }

    @ApiOperation(value = "이메일 인증 코드 확인", notes = "인증 코드의 일치 여부를 확인한다.")
    @Tag(name = "auth")
    @ApiImplicitParam(name = "confirmCode", value = "인증 코드", paramType = "query", required = true)
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)"),
            @ApiResponse(code = 404, message = "인증 코드의 유효 시간이 초과되었습니다."),
            @ApiResponse(code = 409, message = "인증 코드가 일치하지 않습니다.")
    })
    @PostMapping("/email/confirm")
    public void emailConfirm(@RequestBody @Valid ConfirmCodeRequestDto requestDto, HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);

        if (session != null) {
            ConfirmMember confirmMember = (ConfirmMember) session.getAttribute(SessionConst.CONFIRM_MEMBER);
            emailService.checkConfirmMail(confirmMember, requestDto.getConfirmCode());
            session.invalidate();
        } else {
            throw new NoSuchElementException("인증 코드의 유효 시간이 초과되었습니다.");
        }
    }

    @ApiOperation(value = "임시 비밀번호 전송", notes = "임시 비밀번호를 이메일로 전송한다.")
    @Tag(name = "auth")
    @ApiImplicitParam(name = "email", value = "이메일", paramType = "query", required = true)
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
