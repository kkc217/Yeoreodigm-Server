package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Authority;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.RefreshToken;
import com.yeoreodigm.server.domain.SurveyResult;
import com.yeoreodigm.server.domain.board.Follow;
import com.yeoreodigm.server.dto.constraint.EmailConst;
import com.yeoreodigm.server.dto.constraint.MemberConst;
import com.yeoreodigm.server.dto.jwt.TokenDto;
import com.yeoreodigm.server.dto.member.LoginRequestDto;
import com.yeoreodigm.server.dto.member.MemberAuthDto;
import com.yeoreodigm.server.dto.member.MemberJoinRequestDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.exception.LoginRequiredException;
import com.yeoreodigm.server.jwt.CustomEmailPasswordAuthToken;
import com.yeoreodigm.server.jwt.TokenProvider;
import com.yeoreodigm.server.repository.FollowRepository;
import com.yeoreodigm.server.repository.MemberRepository;
import com.yeoreodigm.server.repository.RefreshTokenRepository;
import com.yeoreodigm.server.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final SurveyRepository surveyRepository;

    private final FollowRepository followRepository;

    private final AuthenticationManager authenticationManager;

    private final TokenProvider tokenProvider;

    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenRepository refreshTokenRepository;

    public Member getMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email);

        if (member != null) {
            return member;
        } else {
            throw new BadRequestException("일치하는 회원 정보가 없습니다.");
        }
    }

    public Member getMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId);

        if (member != null) {
            return member;
        } else {
            throw new BadRequestException("일치하는 회원 정보가 없습니다.");
        }
    }

    @Transactional
    public void join(MemberJoinRequestDto memberJoinRequestDto) {
        //중복 검사
        checkDuplicateEmail(memberJoinRequestDto.getEmail());
        checkDuplicateNickname(memberJoinRequestDto.getNickname());

        //비밀번호 암호화
        String password = encodePassword(memberJoinRequestDto.getPassword());

        LocalDate birth = LocalDate.of(memberJoinRequestDto.getYear(), memberJoinRequestDto.getMonth(), memberJoinRequestDto.getDay());

        Member member = new Member(
                memberJoinRequestDto.getEmail(),
                password,
                memberJoinRequestDto.getNickname(),
                birth,
                memberJoinRequestDto.getGender(),
                memberJoinRequestDto.getRegion(),
                memberJoinRequestDto.isOptional()
        );

        memberRepository.saveAndFlush(member);

        surveyRepository.saveAndFlush(new SurveyResult(member));
    }

    public Member login(String email, String password) {
        Member member = memberRepository.findByEmail(email);

        if (member != null && passwordEncoder.matches(password, member.getPassword())) {
            return member;
        }
        throw new BadRequestException("이메일 또는 비밀번호를 잘못 입력했습니다.");
    }

    @Transactional
    public TokenDto loginV2(LoginRequestDto requestDto) {
        CustomEmailPasswordAuthToken customEmailPasswordAuthToken
                = new CustomEmailPasswordAuthToken(requestDto.getEmail(), requestDto.getPassword());
        Authentication authentication = authenticationManager.authenticate(customEmailPasswordAuthToken);

        String email = authentication.getName();
        Member member = memberRepository.findByEmail(email);
        if (Objects.isNull(member)) throw new BadRequestException("일치하는 회원 정보가 없습니다.");

        String accessToken = tokenProvider.createAccessToken(email, member.getAuthority());
        String refreshToken = tokenProvider.createRefreshToken(email, member.getAuthority());

        Optional<RefreshToken> originRefreshTokenOpt = refreshTokenRepository.findByKey(email);
        if (originRefreshTokenOpt.isEmpty()) {
            refreshTokenRepository.save(new RefreshToken(email, refreshToken));
        } else {
            RefreshToken originRefreshToken = originRefreshTokenOpt.get();
            originRefreshToken.changeValue(refreshToken);
            refreshTokenRepository.save(originRefreshToken);
        }

        return tokenProvider.createTokenDto(accessToken, refreshToken);
    }

    public void checkDuplicateEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if (member != null) {
            throw new BadRequestException("이미 등록된 이메일입니다.");
        }
    }

    public void checkDuplicateNickname(String nickname) {
        Member member = memberRepository.findByNickname(nickname);
        if (member != null) {
            throw new BadRequestException("이미 등록된 닉네임입니다.");
        }
    }

    @Transactional
    public void confirmAuth(MemberAuthDto memberAuthDto, String code) {
        if (!Objects.equals(code, memberAuthDto.getConfirmCode())) {
            throw new BadRequestException("인증 코드가 일치하지 않습니다.");
        }

        Member member = getMemberByEmail(memberAuthDto.getEmail());

        member.changeAuthority(Authority.ROLE_SURVEY);
        memberRepository.saveAndFlush(member);
    }

    @Transactional
    public String resetPassword(String email) {
        Member member = getMemberByEmail(email);

        String newPassword = genPassword();

        member.changePassword(encodePassword(newPassword));
        memberRepository.saveAndFlush(member);

        return newPassword;
    }

    private String genPassword() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().split("-")[4];
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public void checkPassword(String password, Member member) {
        if (member == null) throw new LoginRequiredException("로그인이 필요합니다.");

        if (!passwordEncoder.matches(password, member.getPassword()))
            throw new BadRequestException("비밀번호가 일치하지 않습니다.");
    }

    @Transactional
    public void changePassword(String password, Member member) {
        if (member == null) throw new LoginRequiredException("로그인이 필요합니다.");

        member.changePassword(encodePassword(password));
        memberRepository.merge(member);
        memberRepository.flush();
    }

    public Member searchMember(String content) {
        Member member = Pattern.matches(EmailConst.EMAIL_PATTERN, content) ?
                memberRepository.findByEmail(content) :
                memberRepository.findByNickname(content);
        if (member != null) {
            return member;
        } else {
            throw new BadRequestException("일치하는 사용자가 없습니다.");
        }
    }

    public List<Member> searchMembersByNickname(String content, int page, int limit) {
        return memberRepository.findMembersByNickname(content, limit * (page - 1), limit);
    }

    public int checkNextSearchMembersByNickname(String content, int page, int limit) {
        return searchMembersByNickname(content, page + 1, limit).size() > 0 ? page + 1 : 0;
    }

    @Transactional
    public void changeIntroduction(Member member, String newIntroduction) {
        if (member == null) throw new LoginRequiredException("로그인이 필요합니다.");

        member.changeIntroduction(newIntroduction);
        memberRepository.merge(member);
        memberRepository.flush();
    }

    @Transactional
    public void changeProfileImage(Member member, String newProfileImageUrl) {
        member.changeProfileImage(newProfileImageUrl);
        memberRepository.merge(member);
        memberRepository.flush();
    }

    @Transactional
    public void deleteProfileImage(Member member) {
        if (member == null) throw new LoginRequiredException("로그인이 필요합니다.");

        member.changeProfileImage(MemberConst.DEFAULT_PROFILE_IMAGE_URL);
        memberRepository.merge(member);
        memberRepository.flush();
    }

    @Transactional
    public void deleteMember(Member member) {
        if (member == null) throw new LoginRequiredException("로그인이 필요합니다");

        memberRepository.deleteMember(member);
    }

    @Transactional
    public void changeNickname(Member member, String nickname) {
        if (member == null) throw new LoginRequiredException("로그인이 필요합니다.");

        checkDuplicateNickname(nickname);

        member.changeNickname(nickname);
        memberRepository.merge(member);
        memberRepository.flush();
    }

    public Long getFollowerCountByMember(Member member) {
        return followRepository.countFollowerByMember(member);
    }

    public List<Member> getFollowerByMember(Member member) {
        return followRepository.findFollowerByMember(member);
    }


    public Long getFolloweeCountByMember(Member member) {
        return followRepository.countFolloweeByMember(member);
    }

    public List<Member> getFolloweeByMember(Member member) {
        return followRepository.findFolloweeByMember(member);
    }

    @Transactional
    public void changeFollow(Member member, Member followee, boolean isFollow) {
        if (Objects.isNull(member)) throw new LoginRequiredException("로그인이 필요합니다.");
        if (Objects.equals(member.getId(), followee.getId())) throw new BadRequestException("본인은 팔로우할 수 없습니다.");

        Follow follow = followRepository.findByMembers(member, followee);

        if (isFollow) {
            if (Objects.isNull(follow)) {
                Follow newFollow = new Follow(member, followee);
                followRepository.saveAndFlush(newFollow);
            }
        } else if (Objects.nonNull(follow)) {
            followRepository.deleteById(follow.getId());
        }
    }

    public boolean checkFollow(Member member, Member followee) {
        if (Objects.isNull(member)) return false;

        return !Objects.isNull(followRepository.findByMembers(member, followee));
    }

}
