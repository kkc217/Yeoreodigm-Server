package com.yeoreodigm.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoreodigm.server.domain.Gender;
import com.yeoreodigm.server.dto.jwt.TokenDto;
import com.yeoreodigm.server.dto.jwt.TokenMemberInfoDto;
import com.yeoreodigm.server.dto.member.LoginRequestDto;
import com.yeoreodigm.server.dto.member.MemberJoinRequestDto;
import com.yeoreodigm.server.service.MemberService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AuthApiControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private AuthApiController authApiController;

    @Mock
    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authApiController = new AuthApiController(memberService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authApiController).build();
        ObjectMapper objectMapper = new ObjectMapper();
    }

    @Test
    void 회원가입_요청_성공() {
        MemberJoinRequestDto memberJoinRequestDto = new MemberJoinRequestDto();
        memberJoinRequestDto.setEmail("test@naver.com");
        memberJoinRequestDto.setNickname("test");
        memberJoinRequestDto.setPassword("aaaaa");
        memberJoinRequestDto.setGender(Gender.MALE);
        memberJoinRequestDto.setYear(1900);
        memberJoinRequestDto.setMonth(1);
        memberJoinRequestDto.setDay(1);
        memberJoinRequestDto.setRegion("경기");
        memberJoinRequestDto.setOptional(true);

        authApiController.join(memberJoinRequestDto);
    }

    @Test
    void 로그인_요청_성공() {
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("test@naver.com");
        loginRequestDto.setPassword("aaaaa");
        authApiController.login(loginRequestDto);
    }

    @Test
    void 토큰_재발급_요청_성공() {
        authApiController.reissue(new TokenDto("a", "b", "Bearer"));
    }

    @Test
    void 자동_로그인_요청_성공() {
        authApiController.autoLogin(authentication);
    }

}