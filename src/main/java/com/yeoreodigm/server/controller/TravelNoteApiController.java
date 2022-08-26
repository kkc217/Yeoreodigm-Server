package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.*;
import com.yeoreodigm.server.dto.MemberResponseDto;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.constraint.RecommendConst;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.note.*;
import com.yeoreodigm.server.dto.note.comment.CommentResponseDto;
import com.yeoreodigm.server.dto.note.comment.CommentShortResponseDto;
import com.yeoreodigm.server.dto.note.comment.CourseCommentRequestDto;
import com.yeoreodigm.server.dto.noteprepare.NewTravelNoteRequestDto;
import com.yeoreodigm.server.dto.noteprepare.TravelNoteIdResponseDto;
import com.yeoreodigm.server.dto.place.PlaceResponseDto;
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

    @PostMapping("/new")
    public TravelNoteIdResponseDto createNewTravelNote(
            @RequestBody @Valid NewTravelNoteRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return new TravelNoteIdResponseDto(
                travelNoteService.submitNotePrepare(
                        travelNoteService.createTravelNote(member, requestDto)));
    }

    @GetMapping("/{travelNoteId}")
    public TravelNoteInfoResponseDto callTravelMakingNoteInfo(
            @PathVariable("travelNoteId") Long travelNoteId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);

        NoteAuthority noteAuthority = travelNoteService.checkNoteAuthority(member, travelNote);

        return new TravelNoteInfoResponseDto(noteAuthority, travelNote);
    }

    @GetMapping("/course/{travelNoteId}")
    public Result<List<TravelMakingNoteCourseResponseDto>> callTravelMakingNoteCourse(
            @PathVariable("travelNoteId") Long travelNoteId) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);

        List<Course> courseList = courseService.getCoursesByTravelNote(travelNote);
        List<RouteInfoDto> routeInfoList = courseService.getRouteInfosByTravelNote(travelNote);

        List<TravelMakingNoteCourseResponseDto> response = new ArrayList<>();
        int indexStart = 0;
        for (int i = 0; i < courseList.size(); i++) {
            RouteInfoDto routeInfoDto = routeInfoList.get(i);

            Course course = courseList.get(i);
            List<Places> placeList = placeService.getPlacesByCourse(course);

            response.add(new TravelMakingNoteCourseResponseDto(
                    indexStart,
                    course.getDay(),
                    placeList,
                    routeInfoDto.getRouteInfos()));
            indexStart += placeList.size();
        }

        return new Result<>(response);
    }

    @GetMapping("/course/coordinate/{travelNoteId}")
    public Result<List<CourseCoordinateDto>> callTravelMakingNoteCourseCoordinate(
            @PathVariable("travelNoteId") Long travelNoteId) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);

        List<Course> courseList = courseService.getCoursesByTravelNote(travelNote);
        List<String> markerColorList = mapMarkerService.getMarkerColors(courseList.size());

        List<CourseCoordinateDto> response = new ArrayList<>();
        for (Course course : courseList) {
            response.add(new CourseCoordinateDto(
                    course.getDay(),
                    markerColorList.get(course.getDay() - 1),
                    placeService.getPlacesByCourse(course)));
        }

        return new Result<>(response);
    }

    @PostMapping("/course/save/{travelNoteId}")
    public void saveTravelMakingNoteCourse(
            @PathVariable("travelNoteId") Long travelNoteId,
            @RequestBody @Valid List<List<Long>> request) {
        travelNoteService.updateCourse(travelNoteService.getTravelNoteById(travelNoteId), request);
    }

    @GetMapping("/course/optimize/{travelNoteId}")
    public void optimizeCourse(
            @PathVariable("travelNoteId") Long travelNoteId) {
        courseService.optimizeCourse(travelNoteService.getTravelNoteById(travelNoteId));
    }

    @GetMapping("/course/route/{travelNoteId}")
    public Result<List<RouteInfoDto>> callRoutes(
            @PathVariable("travelNoteId") Long travelNoteId) {
        return new Result<>(
                courseService.getRouteInfosByTravelNote(travelNoteService.getTravelNoteById(travelNoteId)));
    }

    @PostMapping("/title/change")
    public void changeTravelMakingNoteTitle(
            @RequestBody @Valid NoteTitleRequestDto requestDto) {
        travelNoteService.changeTitle(
                travelNoteService.getTravelNoteById(requestDto.getTravelNoteId()), requestDto.getNewTitle());
    }

    @PostMapping("/composition/change")
    public void changeTravelMakingNoteComposition(
            @RequestBody @Valid NoteCompositionRequestDto requestDto) {
        travelNoteService.changeComposition(
                travelNoteService.getTravelNoteById(requestDto.getTravelNoteId()),
                requestDto.getAdult(),
                requestDto.getChild(),
                requestDto.getAnimal());
    }

    @PostMapping("/publicshare/change")
    public void changeTravelMakingNotePublicShare(
            @RequestBody @Valid PublicShareRequestDto requestDto) {
        travelNoteService.changePublicShare(
                travelNoteService.getTravelNoteById(requestDto.getTravelNoteId()), requestDto.isPublicShare());
    }

    @GetMapping("/companion/{travelNoteId}")
    public Result<List<MemberResponseDto>> callTravelMakingNoteCompanion(
            @PathVariable("travelNoteId") Long travelNoteId) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);

        List<Member> memberList = travelNoteService.getCompanionMember(travelNote);
        List<MemberResponseDto> response = memberList
                .stream()
                .map(MemberResponseDto::new)
                .toList();

        return new Result<>(response);
    }

    @PostMapping("/companion/add")
    public MemberResponseDto addTravelMakingNoteCompanion(
            @RequestBody @Valid CompanionRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        Member companion = travelNoteService.addNoteCompanion(
                travelNoteService.getTravelNoteById(requestDto.getTravelNoteId()), member, requestDto.getContent());
        return new MemberResponseDto(companion);
    }

    @PostMapping("/companion/delete")
    public void deleteCompanion(
            @RequestBody @Valid CompanionRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        travelNoteService.deleteCompanion(
                travelNoteService.getTravelNoteById(requestDto.getTravelNoteId()), member, requestDto.getMemberId());
    }

    @GetMapping("/comment/{travelNoteId}/{day}")
    public Result<List<CommentResponseDto>> callCourseComment(
            @PathVariable(name = "travelNoteId") Long travelNoteId,
            @PathVariable(name = "day") int day) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);

        List<CourseComment> courseCommentList
                = commentService.getCourseCommentsByTravelNoteAndDay(travelNote, day);

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
        CourseComment courseComment = commentService.addCourseComment(
                travelNoteService.getTravelNoteById(requestDto.getTravelNoteId()),
                member,
                requestDto.getDay(),
                requestDto.getText());

        return new CommentShortResponseDto(courseComment.getId(), courseComment.getCreated());
    }

    @PostMapping("/comment/delete")
    public void deleteCourseComment(
            @RequestBody @Valid CourseCommentRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        commentService.deleteCourseComment(member, requestDto.getCommentId());
    }

    @PostMapping("/place/add")
    public void addRecommendedPlaceToCourse(
            @RequestBody @Valid RecommendPlaceRequestDto requestDto) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(requestDto.getTravelNoteId());

        if (requestDto.getPlaceId() != null) {
            courseService.addPlace(travelNote, requestDto.getDay(), requestDto.getPlaceId());
        } else {
            courseService.addPlaces(travelNote, requestDto.getDay(), requestDto.getPlaceIdList());
        }
    }

    @GetMapping("/place/recommend/{travelNoteId}")
    public Result<List<PlaceResponseDto>> refreshPlaceRecommended(
            @PathVariable("travelNoteId") Long travelNoteId) {
        List<Places> placeList = recommendService.getRecommendedPlacesByTravelNote(
                travelNoteService.getTravelNoteById(travelNoteId), RecommendConst.NOTE_PLACE_RECOMMEND_NUM);

        return new Result<>(placeList.stream().map(PlaceResponseDto::new).toList());
    }

}
