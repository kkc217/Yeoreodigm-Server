package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.SurveyItem;
import com.yeoreodigm.server.domain.SurveyResult;
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

    public List<SurveyItemDto> getSurveyInfo(int group) {
        List<SurveyItem> surveyItems = surveyRepository.findItemsByGroup(group);

        List<SurveyItemDto> result = new ArrayList<>();
        for (SurveyItem item : surveyItems) {
            result.add(new SurveyItemDto(item.getContentId(),
                    item.getTitle(), item.getTag(), item.getImageUrl()));
        }

        return result;
    }

    @Transactional
    public void putSurveyResult(String email, String contentId) {
        SurveyResult surveyResult = surveyRepository.findSurveyResultByEmail(memberRepository.findOneByEmail(email));
        surveyResult.updateProgress();
        surveyResult.changeResult(surveyResult.getResult() + "/" + contentId);
        surveyRepository.saveResult(surveyResult);
    }
}
