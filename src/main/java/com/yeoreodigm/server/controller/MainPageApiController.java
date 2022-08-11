package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.mainpage.MainPageInfoDto;
import com.yeoreodigm.server.service.MainPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main")
public class MainPageApiController {

    private final MainPageService mainPageService;

    @GetMapping("/info")
    public MainPageInfoDto callMainPage(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        if (member != null) {
            return mainPageService.callMainPageMember(member);
        } else {
            return mainPageService.callMainPageVisitor();
        }
    }

}
