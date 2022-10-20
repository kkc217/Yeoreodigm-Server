package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Gender;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.dto.member.MemberJoinRequestDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.FollowRepository;
import com.yeoreodigm.server.repository.MemberRepository;
import com.yeoreodigm.server.repository.SurveyRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@Transactional
public class MemberServiceTest {

    private MemberRepository mockMemberRepository;

    @Mock
    private SurveyRepository surveyRepository;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Before
    public void setUp() {
        Member admin = new Member(
                "yeoreodigm@naver.com",
                "qwer1234!",
                "여러다임",
                LocalDate.now(),
                Gender.MALE,
                "경기",
                true);

        mockMemberRepository = mock(MemberRepository.class);
        when(mockMemberRepository.findByEmail("yeoreodigm@naver.com")).thenReturn(admin);
        when(mockMemberRepository.findByNickname("여러다임")).thenReturn(admin);

        MockitoAnnotations.openMocks(this);
        memberService = new MemberService(
                mockMemberRepository,
                surveyRepository,
                followRepository,
                passwordEncoder);
    }

    @Test(expected = BadRequestException.class)
    public void 회원가입_이메일중복_테스트() {
        MemberJoinRequestDto memberJoinRequestDto = new MemberJoinRequestDto();
        memberJoinRequestDto.setEmail("yeoreodigm@naver.com");
        memberJoinRequestDto.setNickname("여러다임2");
        memberJoinRequestDto.setPassword("qwer1234!");
        memberJoinRequestDto.setGender(Gender.MALE);
        memberJoinRequestDto.setYear(2000);
        memberJoinRequestDto.setMonth(1);
        memberJoinRequestDto.setDay(1);
        memberJoinRequestDto.setRegion("경기");
        memberJoinRequestDto.setOptional(true);

        memberService.join(memberJoinRequestDto);
    }

}