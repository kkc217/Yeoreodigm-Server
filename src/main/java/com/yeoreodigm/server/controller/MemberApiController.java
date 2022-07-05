package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.api.*;
import com.yeoreodigm.server.api.constraint.SessionConst;
import com.yeoreodigm.server.domain.Authority;
import com.yeoreodigm.server.domain.LoginMember;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.service.MemberService;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.NoSuchElementException;

@Tag(name = "auth", description = "인증에 관한 API")
@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

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
    @PostMapping("/api/auth/login")
    public LoginResponseDto login(@RequestBody @Valid LoginRequestDto loginRequestDto, HttpServletRequest request) {
        Member member = memberService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        HttpSession session = request.getSession();
        LoginResponseDto loginResponseDto = new LoginResponseDto(member.getEmail(), member.getNickname(), member.getAuthority(), session.getId());
        session.setAttribute(SessionConst.LOGIN_MEMBER, new LoginMember(member.getEmail(), member.getNickname(), member.getAuthority()));
        return loginResponseDto;
    }

    @ApiOperation(value = "오토 로그인", notes = "세션 정보로 로그인한다.")
    @Tag(name = "auth")
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)"),
            @ApiResponse(code = 404, message = "세션이 만료되었습니다.")
    })
    @PostMapping("/api/auth/autologin")
    public AutoLoginResponseDto autoLogin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            LoginMember loginMember = (LoginMember)session.getAttribute(SessionConst.LOGIN_MEMBER);
            return new AutoLoginResponseDto(loginMember.getEmail(), loginMember.getNickname(), loginMember.getAuthority());
        } else {
            throw new NoSuchElementException("세션이 만료되었습니다.");
        }
    }

    @ApiOperation(value = "이메일 중복 확인", notes = "이미 등록되어 있는 이메일인지 확인한다.")
    @Tag(name = "auth")
    @ApiImplicitParam(name = "email", value = "이메일", paramType = "query", required = true)
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)"),
            @ApiResponse(code = 409, message = "(중복시) 이미 등록된 이메일입니다.")
    })
    @PostMapping("/api/auth/check/email")
    public void checkEmail(@RequestBody @Valid CheckEmailRequestDto request) {
        memberService.validateDuplicateEmail(request.getEmail());
    }

    @ApiOperation(value = "닉네임 중복 확인", notes = "이미 등록되어 있는 닉네임인지 확인한다.")
    @Tag(name = "auth")
    @ApiImplicitParam(name = "nickname", value = "닉네임", paramType = "query", required = true)
    @ApiResponses({
            @ApiResponse(code = 200, message = "(성공)"),
            @ApiResponse(code = 409, message = "(중복시) 이미 등록된 닉네임입니다.")
    })
    @PostMapping("/api/auth/check/nickname")
    public void checkNickname(@RequestBody @Valid CheckNicknameRequestDto request) {
        memberService.validateDuplicateNickname(request.getNickname());
    }

}
