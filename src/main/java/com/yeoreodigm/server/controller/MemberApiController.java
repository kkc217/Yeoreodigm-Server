package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Authority;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.dto.CountDto;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.follow.FollowCheckDto;
import com.yeoreodigm.server.dto.follow.FollowRequestDto;
import com.yeoreodigm.server.dto.member.*;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.exception.LoginRequiredException;
import com.yeoreodigm.server.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

import static com.yeoreodigm.server.dto.constraint.AWSConst.AWS_S3_BASE_URL;
import static com.yeoreodigm.server.dto.constraint.AWSConst.AWS_S3_PROFILE_URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {

    private final MemberService memberService;

    private final SurveyService surveyService;

    private final TravelNoteService travelNoteService;

    private final EmailService emailService;

    private final AwsS3Service awsS3Service;

    @PostMapping("/new")
    public void join(@RequestBody @Valid MemberJoinRequestDto requestDto) {
        memberService.join(requestDto);
    }

    @PostMapping("/login")
    public MemberInfoDto login(
            @RequestBody @Valid LoginRequestDto requestDto,
            HttpServletRequest httpServletRequest) {
        //회원 유무, 비밀번호 일치 확인
        Member member = memberService.login(requestDto.getEmail(), requestDto.getPassword());

        MemberInfoDto responseDto = new MemberInfoDto(member);

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
    public MemberInfoDto autoLogin(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        if (member != null) {
            MemberInfoDto responseDto = new MemberInfoDto(member);

            if (member.getAuthority() == Authority.ROLE_SURVEY) {
                responseDto.setSurveyIndex(surveyService.getProgress(member));
            }

            return responseDto;
        } else {
            throw new LoginRequiredException("다시 로그인해주시기 바랍니다.");
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

    @GetMapping("")
    public MemberInfoDto callMemberInfo(Authentication authentication) {
        return new MemberInfoDto(
                memberService.getMemberByEmail(authentication.getName()));
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

    @PatchMapping("/nickname")
    public void changeNickname(
            @RequestBody HashMap<String, String> request,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        memberService.changeNickname(member, request.get("nickname"));
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

    @PatchMapping("/auth")
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
    public void checkPassword(
            @RequestBody HashMap<String, String> request,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        memberService.checkPassword(request.get("password"), member);
    }

    @PutMapping("/password")
    public void passwordReset(
            @RequestBody HashMap<String, String> request) {
        String email = request.get("email");
        emailService.sendResetMail(email, memberService.resetPassword(email));
    }

    @PatchMapping("/password")
    public void changePassword(
            @RequestBody HashMap<String, String> request,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        memberService.changePassword(request.get("password"), member);
    }

    @GetMapping("/profile")
    public ProfileDto callProfileInfo(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        if (member == null) {
            throw new LoginRequiredException("로그인이 필요합니다.");
        } else {
            return new ProfileDto(member);
        }
    }

    @GetMapping("/profile/{memberId}")
    public ProfileDto callMemberProfileInfo(
            @PathVariable("memberId") Long memberId) {
        return new ProfileDto(memberService.getMemberById(memberId));
    }

    @PatchMapping("/profile/introduction")
    public void changeIntroduction(
            @RequestBody HashMap<String, String> request,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        memberService.changeIntroduction(member, request.get("introduction"));
    }

    @PatchMapping("/profile/image")
    public void changeProfileImage(
            @RequestPart(value = "file") MultipartFile multipartFile,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        if (member == null) throw new LoginRequiredException("로그인이 필요합니다.");

        memberService.changeProfileImage(
                member,
                AWS_S3_BASE_URL
                        + AWS_S3_PROFILE_URI
                        + "/"
                        + awsS3Service.uploadFile(AWS_S3_PROFILE_URI, member.getId().toString(), multipartFile));
    }

    @DeleteMapping("/profile/image")
    public void deleteProfileImage(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        memberService.deleteProfileImage(member);
    }

    @DeleteMapping("")
    public void deleteMember(
            HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);

        if (session != null) {
            Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
            memberService.deleteMember(member);
            travelNoteService.resetTitle(member);
            session.invalidate();
        } else {
            throw new LoginRequiredException("로그인이 필요합니다.");
        }
    }

    @GetMapping("/follower/count/{memberId}")
    public CountDto callFollowerCount(
            @PathVariable("memberId") Long memberId) {
        return new CountDto(
                memberService.getFollowerCountByMember(memberService.getMemberById(memberId)));
    }

    @GetMapping("/follower/{memberId}")
    public Result<List<MemberItemDto>> callFollower(
            @PathVariable("memberId") Long memberId) {
        return new Result<>(
                memberService.getFollowerByMember(memberService.getMemberById(memberId))
                        .stream()
                        .map(MemberItemDto::new)
                        .toList());
    }

    @GetMapping("/followee/count/{memberId}")
    public CountDto callFolloweeCount(
            @PathVariable("memberId") Long memberId) {
        return new CountDto(
                memberService.getFolloweeCountByMember(memberService.getMemberById(memberId)));
    }

    @GetMapping("/followee/{memberId}")
    public Result<List<MemberItemDto>> callFollowee(
            @PathVariable("memberId") Long memberId) {
        return new Result<>(
                memberService.getFolloweeByMember(memberService.getMemberById(memberId))
                        .stream()
                        .map(MemberItemDto::new)
                        .toList());
    }

    @GetMapping("/follow/{memberId}")
    public FollowCheckDto checkFollow(
            @PathVariable("memberId") Long memberId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return new FollowCheckDto(memberService.checkFollow(member, memberService.getMemberById(memberId)));
    }

    @PatchMapping("/follow")
    public void changeFollow(
            @RequestBody @Valid FollowRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        memberService.changeFollow(
                member, memberService.getMemberById(requestDto.getMemberId()), requestDto.isFollow());
    }

}
