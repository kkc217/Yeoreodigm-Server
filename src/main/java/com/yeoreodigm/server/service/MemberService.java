package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Authority;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.SurveyResult;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.MemberRepository;
import com.yeoreodigm.server.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

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
        memberRepository.save(member);

        surveyRepository.saveResult(new SurveyResult(member));
    }

    public void validateDuplicateEmail(String email) {
        List<Member> findMembers = memberRepository.findByEmail(email);
        if (!findMembers.isEmpty()) {
            throw new BadRequestException("이미 등록된 이메일입니다.");
        }
    }

    public void validateDuplicateNickname(String nickname) {
        List<Member> findMembers = memberRepository.findByNickname(nickname);
        if (!findMembers.isEmpty()) {
            throw new BadRequestException("이미 등록된 닉네임입니다.");
        }
    }

    public Member checkLoginInfo(String email, String password) {
        Member member = memberRepository.findOneByEmail(email);
        if (member  == null) {
            throw new BadRequestException("등록된 이메일 정보가 없습니다.");
        }

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new BadRequestException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }

    @Transactional
    public void updateMemberAuthority(String email, Authority authority) {
        Member member = memberRepository.findOneByEmail(email);
        member.changeAuthority(authority);
        memberRepository.save(member);
    }

    @Transactional
    public String resetPassword(String email) {
        Member member = memberRepository.findOneByEmail(email);

        if (member != null) {
            String newPassword = genPassword();
            member.changePassword(encodePassword(newPassword));
            memberRepository.save(member);
            return newPassword;
        } else {
            throw new BadRequestException("등록된 회원 정보가 없습니다.");
        }
    }

    public Member checkMemberByEmail(String email) {
        Member member = memberRepository.findOneByEmail(email);

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

}
