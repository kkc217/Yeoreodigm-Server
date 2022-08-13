package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Course;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.comment.CommentItemDto;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.detail.TravelNoteAndLikeDto;
import com.yeoreodigm.server.dto.detail.TravelNoteDetailInfo;
import com.yeoreodigm.server.dto.detail.NoteDetailResponseDto;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.note.CourseCoordinateDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/detail")
public class DetailPageApiController {

    private final TravelNoteService travelNoteService;

    private final TravelNoteLikeService travelNoteLikeService;

    private final CourseService courseService;

    private final NoteCommentService noteCommentService;

    private final MapMarkerService mapMarkerService;

    private final PlaceService placeService;

    @GetMapping("/travelnote/{travelNoteId}")
    public NoteDetailResponseDto callTravelNoteDetail(
            @PathVariable("travelNoteId") Long travelNoteId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {

        TravelNote travelNote = travelNoteService.findTravelNote(travelNoteId);

        if (!travelNote.isPublicShare()) throw new BadRequestException("이 여행 메이킹 노트는 볼 수 없습니다.");

        TravelNoteDetailInfo travelNoteInfo = travelNoteService.getTravelNoteInfo(travelNote);

        LikeItemDto travelNoteLikeInfo = travelNoteLikeService.getLikeInfo(travelNoteId, null);

        List<Course> courseList = courseService.searchCourse(travelNoteId);
        List<String> markerColorList = mapMarkerService.getMarkerColorList(courseList.size());
        List<CourseCoordinateDto> coordinateDtoList = new ArrayList<>();
        for (Course course : courseList) {
            coordinateDtoList.add(new CourseCoordinateDto(
                    course.getDay(),
                    markerColorList.get(course.getDay() - 1),
                    placeService.searchPlacesByCourse(course)));
        }

        //여행 노트 추천 - AI API 구현시 수정 예정
        List<TravelNoteAndLikeDto> recommendedNoteList = travelNoteService.getTempTravelNoteList(4, member);

        List<CommentItemDto> commentList = noteCommentService.getNoteCommentInfo(travelNoteId, member.getId());

        return new NoteDetailResponseDto(
                travelNoteInfo, travelNoteLikeInfo, coordinateDtoList, recommendedNoteList, commentList);
    }

}
