package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Authority;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.SurveyResult;
import com.yeoreodigm.server.dto.constraint.EmailConst;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.MemberRepository;
import com.yeoreodigm.server.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final SurveyRepository surveyRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void join(Member member) {
        //중복 검사
        validateDuplicateEmail(member.getEmail());
        validateDuplicateNickname(member.getNickname());

        //비밀번호 암호화
        member.changePassword(encodePassword(member.getPassword()));

        if (member.getNickname().equals("admin")) {
            //관리자 권한 부여
            member.changeAuthority(Authority.ROLE_ADMIN);
        }

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

    public void validateDuplicateEmail(String email) {
        Member findMembers = memberRepository.findByEmail(email);
        if (findMembers != null) {
            throw new BadRequestException("이미 등록된 이메일입니다.");
        }
    }

    public void validateDuplicateNickname(String nickname) {
        Member findMember = memberRepository.findByNickname(nickname);
        if (findMember != null) {
            throw new BadRequestException("이미 등록된 닉네임입니다.");
        }
    }

    @Transactional
    public void updateMemberAuthority(String email, Authority authority) {
        Member member = memberRepository.findByEmail(email);
        if (member != null) {
            member.changeAuthority(authority);
            memberRepository.saveAndFlush(member);
        } else {
            throw new BadRequestException("일치하는 이메일 정보가 없습니다.");
        }
    }

    @Transactional
    public String resetPassword(String email) {
        Member member = memberRepository.findByEmail(email);

        if (member != null) {
            String newPassword = genPassword();
            member.changePassword(encodePassword(newPassword));
            memberRepository.saveAndFlush(member);
            return newPassword;
        } else {
            throw new BadRequestException("등록된 회원 정보가 없습니다.");
        }
    }

    public Member checkMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email);

        if (member != null) {
            return member;
        } else {
            throw new BadRequestException("등록된 이메일 정보가 없습니다.");
        }
    }

    private String genPassword() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().split("-")[4];
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public Member searchMember(String content) {

        if (Pattern.matches(EmailConst.EMAIL_PATTERN, content)) {
            return memberRepository.findByEmail(content);
        } else {
            return memberRepository.findByNickname(content);
        }

    }

}
