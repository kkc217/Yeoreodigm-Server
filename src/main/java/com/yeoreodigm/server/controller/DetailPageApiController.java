package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Course;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.comment.CommentItemDto;
import com.yeoreodigm.server.dto.constraint.DetailPageConst;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.detail.*;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.note.CourseCoordinateDto;
import com.yeoreodigm.server.dto.note.RouteInfoDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        List<CourseCoordinateDto> coordinateDtoList = courseList.stream().map(course -> new CourseCoordinateDto(
                course.getDay(),
                markerColorList.get(course.getDay() - 1),
                placeService.searchPlacesByCourse(course))).collect(Collectors.toList());

        //여행 노트 추천 - AI API 구현시 수정 예정
        List<TravelNoteAndLikeDto> recommendedNoteList = travelNoteService.getTempTravelNoteList(4, member);

        List<CommentItemDto> commentList = noteCommentService.getNoteCommentInfo(travelNoteId, member.getId());

        return new NoteDetailResponseDto(
                travelNoteInfo, travelNoteLikeInfo, coordinateDtoList, recommendedNoteList, commentList);
    }

    @GetMapping("/travelnote/course/{travelNoteId}/{page}")
    public PageResult<List<NoteDetailCourseResponseDto>> callTravelNoteDetailCourse(
            @PathVariable("travelNoteId") Long travelNoteId,
            @PathVariable("page") int page) {
        List<Course> courseList = courseService.searchCoursePaging(
               travelNoteId, page, DetailPageConst.NOTE_COURSE_PAGING_LIMIT);
        List<RouteInfoDto> routeInfoList = courseService.callRoutesByCourseList(courseList);

        List<NoteDetailCourseResponseDto> response = new ArrayList<>();
        for (int i = 0; i < courseList.size(); i++) {
            RouteInfoDto routeInfoDto = routeInfoList.get(i);
            List<Places> placesList = placeService.searchPlacesByCourse(courseList.get(i));
            response.add(new NoteDetailCourseResponseDto(routeInfoDto, placesList));
        }

        int next = courseService.checkNextCoursePage(
                travelNoteId, page, DetailPageConst.NOTE_COURSE_PAGING_LIMIT);

        return new PageResult<>(response, next);
    }

    @GetMapping("/travelnote/comment/{travelNoteId}")
    public Result<List<CommentItemDto>> callTravelNoteComment(
            @PathVariable("travelNoteId") Long travelNoteId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return new Result<>(noteCommentService.getNoteCommentInfo(travelNoteId, member.getId()));
    }

    @PostMapping("/travelnote/comment/add")
    public CommentItemDto addTravelNoteComment(
            @RequestBody @Valid AddNoteCommentRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        if (member == null) throw new BadRequestException("로그인이 필요한 기능입니다.");

        noteCommentService.addNoteComment(member, requestDto.getTravelNoteId(), requestDto.getText());
        return null;
    }

}
