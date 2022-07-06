package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Authority;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void join(Member member) {
        validateDuplicateEmail(member.getEmail());
        validateDuplicateNickname(member.getNickname());
        String encodedPassword = encodePassword(member.getPassword());
        member.updatePassword(encodedPassword);
        if (member.getNickname().equals("admin")) {
            member.updateAuthority(Authority.ROLE_ADMIN);
        }
        memberRepository.save(member);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public void validateDuplicateEmail(String email) {
        List<Member> findMembers = memberRepository.findByEmail(email);
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 등록된 이메일입니다.");
        }
    }

    public void validateDuplicateNickname(String nickname) {
        List<Member> findMembers = memberRepository.findByNickname(nickname);
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 등록된 닉네임입니다.");
        }
    }

    public Member login(String email, String password) {
        Member member = memberRepository.findOneByEmail(email);
        if (member  == null) {
            throw new NoSuchElementException("등록된 이메일 정보가 없습니다.");
        }

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }

}
