package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.*;
import com.yeoreodigm.server.dto.MemberResponseDto;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.note.*;
import com.yeoreodigm.server.dto.note.comment.CommentResponseDto;
import com.yeoreodigm.server.dto.note.comment.CommentShortResponseDto;
import com.yeoreodigm.server.dto.note.comment.CourseCommentRequestDto;
import com.yeoreodigm.server.dto.search.SearchPlacesResponseDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/note")
public class TravelNoteApiController {

    private final TravelNoteService travelNoteService;

    private final PlaceService placeService;

    private final CourseService courseService;

    private final CourseCommentService commentService;

    private final RecommendService recommendService;

    private final MapMarkerService mapMarkerService;

    @GetMapping("/{travelNoteId}")
    public CallNoteInfoResponseDto callNoteInfo(
            @PathVariable("travelNoteId") Long travelNoteId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {

        TravelNote travelNote = travelNoteService.callNote(travelNoteId);
        NoteAuthority noteAuthority = travelNoteService.checkNoteAuthority(member, travelNote);

        if (noteAuthority == NoteAuthority.ROLE_OWNER) {
            List<Places> placesRecommended = recommendService.getRecommendedPlaces(travelNote);
            return new CallNoteInfoResponseDto(noteAuthority, travelNote, placesRecommended);
        } else if (noteAuthority == NoteAuthority.ROLE_COMPANION) {
            return new CallNoteInfoResponseDto(noteAuthority, travelNote);
        } else {
            return new CallNoteInfoResponseDto(noteAuthority, travelNote);
        }
    }

    @GetMapping("/course/{travelNoteId}")
    public Result<List<CallNoteCoursePagingResponseDto>> callNoteCoursePaging(
            @PathVariable("travelNoteId") Long travelNoteId) {

        List<Course> courseList = courseService.searchCourse(travelNoteId);
        List<RouteInfoDto> routeInfoList = courseService.callRoutes(travelNoteId);

        List<CallNoteCoursePagingResponseDto> response = new ArrayList<>();
        int indexStart = 0;
        for (int i = 0; i < courseList.size(); i++) {
            RouteInfoDto routeInfoDto = routeInfoList.get(i);

            Course course = courseList.get(i);
            List<Places> placeList = placeService.searchPlacesByCourse(course);

            response.add(new CallNoteCoursePagingResponseDto(
                    indexStart,
                    course.getDay(),
                    placeList,
                    routeInfoDto.getRouteInfos()));
            indexStart += placeList.size();
        }

        return new Result<>(response);
    }

    @GetMapping("/course/coordinate/{travelNoteId}")
    public Result<List<CallNoteCourseResponseDto>> callNoteCourse(
            @PathVariable("travelNoteId") Long travelNoteId) {

        List<Course> courseList = courseService.searchCourse(travelNoteId);
        List<String> markerColorList = mapMarkerService.getMarkerColorList(courseList.size());

        List<CallNoteCourseResponseDto> response = new ArrayList<>();
        for (Course course : courseList) {
            response.add(new CallNoteCourseResponseDto(course.getDay(), markerColorList.get(course.getDay() - 1), placeService.searchPlacesByCourse(course)));
        }

        return new Result<>(response);

    }

    @PostMapping("/course/save/{travelNoteId}")
    public void saveNoteCourse(
            @PathVariable("travelNoteId") Long travelNoteId,
            @RequestBody @Valid List<List<Long>> request) {
        travelNoteService.updateNoteCourse(travelNoteId, request);
    }

    @PostMapping("/title/change")
    public void changeNoteTitle(
            @RequestBody @Valid ChangeNoteTitleRequestDto requestDto) {
        travelNoteService.changeTitle(requestDto.getTravelNoteId(), requestDto.getNewTitle());
    }

    @PostMapping("/composition/change")
    public void changeNoteComposition(
            @RequestBody @Valid ChangeNoteCompositionRequestDto requestDto) {
        travelNoteService.changeComposition(requestDto.getTravelNoteId(), requestDto.getAdult(), requestDto.getChild(), requestDto.getAnimal());
    }

    @PostMapping("/publicshare/change")
    public void changePublicShare(
            @RequestBody @Valid ChangePublicShareRequestDto requestDto) {
        travelNoteService.changePublicShare(requestDto.getTravelNoteId(), requestDto.isPublicShare());
    }

    @GetMapping("/companion/{travelNoteId}")
    public Result<List<MemberResponseDto>> callCompanion(
            @PathVariable("travelNoteId") Long travelNoteId) {

        List<Member> memberList = travelNoteService.findCompanion(travelNoteId);
        List<MemberResponseDto> response = memberList
                .stream()
                .map(MemberResponseDto::new)
                .toList();

        return new Result<>(response);

    }

    @PostMapping("/companion/add")
    public MemberResponseDto addCompanion(
            @RequestBody @Valid ChangeCompanionRequestDto requestDto) {
        Member member = travelNoteService.addNoteCompanion(requestDto.getTravelNoteId(), requestDto.getContent());
        return new MemberResponseDto(member);
    }

    @PostMapping("/companion/delete")
    public void deleteCompanion(
            @RequestBody @Valid ChangeCompanionRequestDto requestDto) {
        travelNoteService.deleteCompanion(requestDto.getTravelNoteId(), requestDto.getMemberId());
    }

    @GetMapping("/comment/{travelNoteId}/{day}")
    public Result<List<CommentResponseDto>> callComment(
            @PathVariable(name = "travelNoteId") Long travelNoteId,
            @PathVariable(name = "day") int day) {
        List<CourseComment> courseCommentList
                = commentService.searchCourseCommentByNoteAndDay(travelNoteId, day);

        return new Result<>(
                courseCommentList
                        .stream()
                        .map(CommentResponseDto::new)
                        .toList());
    }

    @PostMapping("/comment/add")
    public CommentShortResponseDto addCourseComment(
            @RequestBody @Valid CourseCommentRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {

        if (member == null) {
            throw new BadRequestException("세션이 만료되었습니다.");
        }

        CourseComment courseComment = commentService.saveCourseComment(
                requestDto.getTravelNoteId(),
                requestDto.getDay(),
                member,
                requestDto.getText());

        return new CommentShortResponseDto(courseComment.getId(), courseComment.getCreated());

    }

    @PostMapping("/comment/delete")
    public void deleteCourseComment(
            @RequestBody @Valid CourseCommentRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        if (member == null) {
            throw new BadRequestException("세션이 만료되었습니다.");
        }
        commentService.deleteCourseComment(requestDto.getCommentId(), member);
    }

    @PostMapping("/place/add")
    public void addRecommendedPlace(
            @RequestBody @Valid AddRecommendRequestDto requestDto) {
        if (requestDto.getPlaceId() != null) {
            courseService.addPlace(requestDto.getTravelNoteId(), requestDto.getDay(), requestDto.getPlaceId());
        } else {
            courseService.addPlaceList(requestDto.getTravelNoteId(), requestDto.getDay(), requestDto.getPlaceIdList());
        }
    }

    @GetMapping("/place/recommend/{travelNoteId}")
    public Result<List<SearchPlacesResponseDto>> resetPlaceRecommended(
            @PathVariable("travelNoteId") Long travelNoteId) {
        TravelNote travelNote = travelNoteService.findTravelNote(travelNoteId);
        return new Result<>(recommendService.getRecommendedPlaces(travelNote)
                        .stream().map(SearchPlacesResponseDto::new).toList());
    }

    @GetMapping("/course/route/{travelNoteId}")
    public Result<List<RouteInfoDto>> callRoutes(
            @PathVariable("travelNoteId") Long travelNoteId) {
        return new Result<>(courseService.callRoutes(travelNoteId));
    }

}
