package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Authority;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.SurveyItem;
import com.yeoreodigm.server.domain.SurveyResult;
import com.yeoreodigm.server.dto.constraint.SurveyConst;
import com.yeoreodigm.server.dto.surveypage.SurveyItemDto;
import com.yeoreodigm.server.repository.MemberRepository;
import com.yeoreodigm.server.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    
    private final MemberRepository memberRepository;

    public List<SurveyItemDto> getSurveyInfo(int progress) {
        List<SurveyItem> surveyItems = surveyRepository.findItemsByGroup(progress);

        List<SurveyItemDto> result = new ArrayList<>();
        for (SurveyItem item : surveyItems) {
            result.add(new SurveyItemDto(item));
        }

        return result;
    }

    @Transactional
    public void submitSurveyResult(Member member, String contentId, int progress) {
        SurveyResult surveyResult = surveyRepository.findSurveyResult(member);
        surveyResult.changeProgress(progress + 1);
        surveyResult.addResult(contentId);

        if (progress == SurveyConst.MAX_SURVEY) {
            Member loginMember = memberRepository.findByEmail(member.getEmail());
            loginMember.changeAuthority(Authority.ROLE_USER);
            memberRepository.save(loginMember);
        }

        surveyRepository.saveAndFlush(surveyResult);
    }

    public int getProgress(Member member) {
        return surveyRepository.findSurveyResult(member).getProgress();
    }

}
