package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Authority;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.SurveyResult;
import com.yeoreodigm.server.dto.constraint.EmailConst;
import com.yeoreodigm.server.dto.member.MemberAuthDto;
import com.yeoreodigm.server.dto.member.MemberJoinRequestDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.MemberRepository;
import com.yeoreodigm.server.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final SurveyRepository surveyRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    public Member getMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email);

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
    public void resetPassword(String email) {
        Member member = getMemberByEmail(email);

        String newPassword = genPassword();

        member.changePassword(encodePassword(newPassword));
        memberRepository.saveAndFlush(member);

        emailService.sendResetMail(email, newPassword);
    }

    private String genPassword() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().split("-")[4];
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
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

    public void changeIntroduction(Member member, String newIntroduction) {
        if (member == null) throw new BadRequestException("로그인이 필요합니다.");

        member.changeIntroduction(newIntroduction);
        memberRepository.merge(member);
    }
}
