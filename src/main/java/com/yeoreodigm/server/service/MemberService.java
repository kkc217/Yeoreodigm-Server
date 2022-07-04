package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        memberRepository.save(member);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public void validateDuplicateEmail(String email) {
        List<Member> findMembers = memberRepository.findByEmail(email);
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }
    }

    public void validateDuplicateNickname(String nickname) {
        List<Member> findMembers = memberRepository.findByNickname(nickname);
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 닉네임입니다.");
        }
    }

}
