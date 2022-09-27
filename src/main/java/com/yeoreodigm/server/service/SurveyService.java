package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Authority;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.SurveyResult;
import com.yeoreodigm.server.dto.constraint.SurveyConst;
import com.yeoreodigm.server.dto.survey.SurveyItemDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.exception.LoginRequiredException;
import com.yeoreodigm.server.repository.MemberRepository;
import com.yeoreodigm.server.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    
    private final MemberRepository memberRepository;

    public List<SurveyItemDto> getSurveyItemsByProgress(int progress) {
        return surveyRepository
                .findSurveyItemsByProgress(progress)
                .stream()
                .map(SurveyItemDto::new)
                .toList();
    }

    @Transactional
    public void submitSurveyResult(Member member, Long contentId, int progress) {
        if (member == null) throw new LoginRequiredException("로그인이 필요합니다.");

        SurveyResult surveyResult = surveyRepository.findSurveyResultByMember(member);
        surveyResult.changeProgress(progress + 1);

        List<Long> resultList = surveyResult.getResult();
        resultList.add(contentId);
        surveyResult.changeResult(resultList);

        if (SurveyConst.MAX_SURVEY == progress) {
            member.changeAuthority(Authority.ROLE_USER);
            memberRepository.merge(member);
        }

        surveyRepository.saveAndFlush(surveyResult);
    }

    public int getProgress(Member member) {
        if (member == null) throw new LoginRequiredException("로그인이 필요합니다.");

        return surveyRepository.findSurveyResultByMember(member).getProgress();
    }

}
