package com.yeoreodigm.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoreodigm.server.domain.Gender;
import com.yeoreodigm.server.dto.member.MemberJoinRequestDto;
import com.yeoreodigm.server.service.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class MemberApiControllerTest {

    @Mock
    private MemberService memberService;

    @Mock
    private SurveyService surveyService;

    @Mock
    private TravelNoteService travelNoteService;

    @Mock
    private EmailService emailService;

    @Mock
    private AwsS3Service awsS3Service;

    @InjectMocks
    private MemberApiController memberApiController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private MemberJoinRequestDto memberJoinRequestDto;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        memberApiController
                = new MemberApiController(memberService, surveyService, travelNoteService, emailService, awsS3Service);
        mockMvc = MockMvcBuilders.standaloneSetup(memberApiController).build();
        objectMapper = new ObjectMapper();

        memberJoinRequestDto = new MemberJoinRequestDto();
        memberJoinRequestDto.setGender(Gender.male);
        memberJoinRequestDto.setYear(2000);
        memberJoinRequestDto.setMonth(1);
        memberJoinRequestDto.setDay(1);
        memberJoinRequestDto.setRegion("경기");
        memberJoinRequestDto.setOptional(true);
    }

    @Test
    public void 회원가입_테스트() {
        memberJoinRequestDto.setEmail("kkc217123@naver.com");
        memberJoinRequestDto.setNickname("testJoin");
        memberJoinRequestDto.setPassword("qwer1234!");
        memberApiController.join(memberJoinRequestDto);

        verify(memberService).join(memberJoinRequestDto);
    }

    @Test
    public void 회원가입_파라미터누락_테스트() throws Exception {
        memberJoinRequestDto.setNickname("testJoin");
        memberJoinRequestDto.setPassword("qwer1234!");

        mockMvc.perform(post("/api/member/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberJoinRequestDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

}