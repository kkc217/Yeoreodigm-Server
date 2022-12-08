package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.dto.CountDto;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.follow.FollowCheckDto;
import com.yeoreodigm.server.dto.follow.FollowRequestDto;
import com.yeoreodigm.server.dto.member.MemberAuthDto;
import com.yeoreodigm.server.dto.member.MemberInfoDto;
import com.yeoreodigm.server.dto.member.MemberItemDto;
import com.yeoreodigm.server.dto.member.ProfileDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.service.AwsS3Service;
import com.yeoreodigm.server.service.EmailService;
import com.yeoreodigm.server.service.MemberService;
import com.yeoreodigm.server.service.TravelNoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.yeoreodigm.server.dto.constraint.AWSConst.AWS_S3_BASE_URL;
import static com.yeoreodigm.server.dto.constraint.AWSConst.AWS_S3_PROFILE_URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@Tag(name = "Member", description = "멤버 API")
public class MemberApiController {

    private final MemberService memberService;

    private final TravelNoteService travelNoteService;

    private final EmailService emailService;

    private final AwsS3Service awsS3Service;

    @GetMapping("")
    @Operation(summary = "멤버 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public MemberInfoDto callMemberInfo(Authentication authentication) {
        return new MemberInfoDto(
                memberService.getMemberByAuth(authentication));
    }

    @PostMapping("/email")
    @Operation(summary = "이메일 중복 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public void checkEmail(
            @RequestBody HashMap<String, String> request) {
        memberService.checkDuplicateEmail(request.get("email"));
    }

    @PostMapping("/nickname")
    @Operation(summary = "닉네임 중복 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public void checkNickname(
            @RequestBody HashMap<String, String> request) {
        memberService.checkDuplicateNickname(request.get("nickname"));
    }

    @PatchMapping("/nickname")
    @Operation(summary = "닉네임 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void changeNickname(
            Authentication authentication,
            @RequestBody HashMap<String, String> request) {
        memberService.changeNickname(memberService.getMemberByAuth(authentication), request.get("nickname"));
    }

    @PostMapping("/auth")
    @Operation(summary = "이메일 인증 코드 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
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

    @PatchMapping("/auth")
    @Operation(summary = "이메일 인증 코드 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public void confirmAuth(
            @RequestBody HashMap<String, String> request,
            HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);

        if (session != null) {
            MemberAuthDto memberAuthDto = (MemberAuthDto) session.getAttribute(SessionConst.CONFIRM_MEMBER);
            memberService.confirmAuth(memberAuthDto, request.get("code"));
            session.invalidate();
        } else {
            throw new BadRequestException("인증 코드의 유효 시간이 초과되었습니다.");
        }
    }

    @PostMapping("/password")
    @Operation(summary = "비밀번호 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void checkPassword(
            Authentication authentication,
            @RequestBody HashMap<String, String> request) {
        memberService.checkPassword(request.get("password"), memberService.getMemberByAuth(authentication));
    }

    @PutMapping("/password")
    @Operation(summary = "비밀번호 초기화")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public void passwordReset(
            @RequestBody HashMap<String, String> request) {
        String email = request.get("email");
        emailService.sendResetMail(email, memberService.resetPassword(email));
    }

    @PatchMapping("/password")
    @Operation(summary = "비밀번호 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void changePassword(
            Authentication authentication,
            @RequestBody HashMap<String, String> request) {
        memberService.changePassword(request.get("password"), memberService.getMemberByAuth(authentication));
    }

    @GetMapping("/profile")
    @Operation(summary = "내 프로필 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public ProfileDto callProfileInfo(Authentication authentication) {
        return new ProfileDto(memberService.getMemberByAuth(authentication));
    }

    @GetMapping("/profile/{memberId}")
    @Operation(summary = "프로필 정보 조회 (멤버 상세 페이지)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public ProfileDto callMemberProfileInfo(
            @PathVariable("memberId") Long memberId) {
        return new ProfileDto(memberService.getMemberById(memberId));
    }

    @PatchMapping("/profile/introduction")
    @Operation(summary = "자기 소개 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void changeIntroduction(
            Authentication authentication,
            @RequestBody HashMap<String, String> request) {
        memberService.changeIntroduction(memberService.getMemberByAuth(authentication), request.get("introduction"));
    }

    @PatchMapping("/profile/image")
    @Operation(summary = "프로필 사진 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void changeProfileImage(
            Authentication authentication,
            @RequestPart(name = "file") MultipartFile multipartFile) {
        Member member = memberService.getMemberByAuth(authentication);

        memberService.changeProfileImage(
                member,
                AWS_S3_BASE_URL
                        + AWS_S3_PROFILE_URI
                        + "/"
                        + awsS3Service.uploadFile(AWS_S3_PROFILE_URI, member.getId().toString(), multipartFile));
    }

    @DeleteMapping("/profile/image")
    @Operation(summary = "프로필 사진 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void deleteProfileImage(
            Authentication authentication) {
        memberService.deleteProfileImage(memberService.getMemberByAuth(authentication));
    }

    @DeleteMapping("")
    @Operation(summary = "회원 탈퇴")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void deleteMember(
            HttpServletResponse response,
            Authentication authentication,
            HttpServletRequest httpServletRequest) {
        Member member = memberService.getMemberByAuth(authentication);
        memberService.deleteMember(member);
        travelNoteService.resetTitle(member);

        HttpSession session = httpServletRequest.getSession(false);
        if (Objects.nonNull(session))
            session.invalidate();

        Cookie cookie = new Cookie("remember-me", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        SecurityContextHolder.clearContext();
    }

    @GetMapping("/follower/my/count")
    @Operation(summary = "내 팔로워 수 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public CountDto callFollowerCount(
            Authentication authentication) {
        return new CountDto(
                memberService.getFollowerCountByMember(memberService.getMemberByAuth(authentication)));
    }

    @GetMapping("/follower/my")
    @Operation(summary = "내 팔로워 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public Result<List<MemberItemDto>> callFollower(
            Authentication authentication) {
        return new Result<>(
                memberService.getFollowerByMember(memberService.getMemberByAuth(authentication))
                        .stream()
                        .map(MemberItemDto::new)
                        .toList());
    }

    @GetMapping("/followee/my/count")
    @Operation(summary = "내 팔로잉 수 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public CountDto callFolloweeCount(
            Authentication authentication) {
        return new CountDto(
                memberService.getFolloweeCountByMember(memberService.getMemberByAuth(authentication)));
    }

    @GetMapping("/followee/my")
    @Operation(summary = "내 팔로잉 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public Result<List<MemberItemDto>> callFollowee(
            Authentication authentication) {
        return new Result<>(
                memberService.getFolloweeByMember(memberService.getMemberByAuth(authentication))
                        .stream()
                        .map(MemberItemDto::new)
                        .toList());
    }

    @GetMapping("/follow/{memberId}")
    @Operation(summary = "팔로우 여부 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public FollowCheckDto checkFollow(
            Authentication authentication,
            @PathVariable("memberId") Long memberId) {
        return new FollowCheckDto(memberService.checkFollow(
                        memberService.getMemberByAuth(authentication),
                        memberService.getMemberById(memberId)));
    }

    @PatchMapping("/follow")
    @Operation(summary = "팔로우 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void changeFollow(
            Authentication authentication,
            @RequestBody @Valid FollowRequestDto requestDto) {
        memberService.changeFollow(
                memberService.getMemberByAuth(authentication),
                memberService.getMemberById(requestDto.getMemberId()), requestDto.isFollow());
    }

}
