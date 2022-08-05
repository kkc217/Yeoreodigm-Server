package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.dto.constraint.WebClientConst;
import com.yeoreodigm.server.dto.recommend.CoursesDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecommendService {

    private final WebClient webClient;

    public List<List<Long>> getRecommendedCourses(Member member, LocalDate dayStart, LocalDate dayEnd, List<Long> places) {

        int day = Period.between(dayStart, dayEnd).getDays() + 1;
        StringBuilder include = new StringBuilder();

        if (places.size() > 0) {
            for (Long placeId : places) {
                include.append(placeId).append(",");
            }
            include.delete(include.length() - 1, include.length());
        } else {
            include.append(0);
        }

        return Objects.requireNonNull(webClient
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path(WebClientConst.COURSE_RECOMMEND_URI)
                                .queryParam("id", member.getId())
                                .queryParam("day", day)
                                .queryParam("include", include)
                                .build())
                        .retrieve()
                        .bodyToMono(CoursesDto.class)
                        .block())
                .getCourseList();

    }

}