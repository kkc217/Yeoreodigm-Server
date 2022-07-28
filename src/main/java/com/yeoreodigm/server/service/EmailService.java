package com.yeoreodigm.server.service;

import com.yeoreodigm.server.dto.constraint.EmailConst;
import com.yeoreodigm.server.dto.ConfirmMemberDto;
import com.yeoreodigm.server.domain.Authority;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmailService {

    private final MemberRepository memberRepository;

    private final JavaMailSender javaMailSender;

    private final TemplateEngine templateEngine;

    private final MemberService memberService;

    public String sendConfirmMail(String email) {
        Member member = memberRepository.findByEmail(email);
        String confirmCode = genRandomCode();

        if (member != null) {
            Context context = new Context();
            context.setVariable("confirmCode", confirmCode);

            String text = templateEngine.process("confirmCodeMail", context);

            sendMail(email, EmailConst.CONFIRM_SUBJECT, text, EmailConst.HTML);

            return confirmCode;
        } else {
            throw new BadRequestException("등록된 회원 정보가 없습니다.");
        }
    }

    @Transactional
    public void checkConfirmMail(ConfirmMemberDto confirmMemberDto, String confirmCode) {
        if (confirmCode.equals(confirmMemberDto.getConfirmCode())) {
            memberService.updateMemberAuthority(confirmMemberDto.getEmail(), Authority.ROLE_SURVEY);
        } else {
            throw new BadRequestException("인증 코드가 일치하지 않습니다.");
        }
    }

    @Transactional
    public void sendResetMail(String email) {
        String newPassword = memberService.resetPassword(email);

        Context context = new Context();
        context.setVariable("newPassword", newPassword);

        String text = templateEngine.process("resetPasswordMail", context);

        sendMail(email, EmailConst.RESET_SUBJECT, text, EmailConst.HTML);

    }

    public void sendMail(String toAddress, String subject, String text, boolean isHtml) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(EmailConst.FROM_ADDRESS);
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setText(text, isHtml);

            javaMailSender.send(message);
        } catch (Exception e) {
            throw new BadRequestException("인증 메일 전송을 실패하였습니다.");
        }
    }

    public String genRandomCode() {
        return Integer.toString((int)((1 + Math.random()) * EmailConst.CODE_SIZE)).substring(1);
    }

}
