package com.yeoreodigm.server.service;

import com.yeoreodigm.server.api.constraint.EmailConst;
import com.yeoreodigm.server.api.session.ConfirmMember;
import com.yeoreodigm.server.domain.Authority;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmailService {

    private final MemberRepository memberRepository;

    private final JavaMailSender javaMailSender;

    public void sendConfirmMail(String email, String confirmCode) {
        Member member = memberRepository.findOneByEmail(email);

        if (member != null) {
            sendMail(email, EmailConst.CONFIRM_SUBJECT, confirmCode);
        } else {
            throw new NoSuchElementException("등록된 회원 정보가 없습니다.");
        }
    }

    @Transactional
    public void checkConfirmMail(ConfirmMember confirmMember, String confirmCode) {
        Member member = memberRepository.findOneByEmail(confirmMember.getEmail());

        if (confirmCode.equals(confirmMember.getConfirmCode())) {
            member.updateAuthority(Authority.ROLE_USER);
        } else {
            throw new IllegalStateException("인증 코드가 일치하지 않습니다.");
        }

    }

    public void sendMail(String toAddress, String subject, String text) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(EmailConst.FROM_ADDRESS);
            simpleMailMessage.setTo(toAddress);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(text);
            javaMailSender.send(simpleMailMessage);
        } catch (Exception e) {
            throw new IllegalStateException("인증 메일 전송을 실패하였습니다.");
        }
    }

    public String genRandomCode() {
        return Integer.toString((int)((1 + Math.random()) * EmailConst.CODE_SIZE)).substring(1);
    }

}
