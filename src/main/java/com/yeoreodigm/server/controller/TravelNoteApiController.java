package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.*;
import com.yeoreodigm.server.dto.ContentRequestDto;
import com.yeoreodigm.server.dto.MemberResponseDto;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.constraint.MainPageConst;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.detail.travelnote.LikeRequestDto;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.mainpage.TravelNoteItemDto;
import com.yeoreodigm.server.dto.note.*;
import com.yeoreodigm.server.dto.note.comment.CommentResponseDto;
import com.yeoreodigm.server.dto.note.comment.CourseCommentRequestDto;
import com.yeoreodigm.server.dto.noteprepare.NewTravelNoteRequestDto;
import com.yeoreodigm.server.dto.noteprepare.TravelNoteIdResponseDto;
import com.yeoreodigm.server.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/note")
public class TravelNoteApiController {

    private final TravelNoteService travelNoteService;

    private final TravelNoteLikeService travelNoteLikeService;

    private final PlaceService placeService;

    private final CourseService courseService;

    private final CourseCommentService commentService;

    private final RecommendService recommendService;

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

    @PatchMapping("/title")
    public void changeTravelMakingNoteTitle(
            @RequestBody @Valid NoteTitleRequestDto requestDto) {
        travelNoteService.changeTitle(
                travelNoteService.getTravelNoteById(requestDto.getTravelNoteId()), requestDto.getNewTitle());
    }

    @PatchMapping("/composition")
    public void changeTravelMakingNoteComposition(
            @RequestBody @Valid NoteCompositionRequestDto requestDto) {
        travelNoteService.changeComposition(
                travelNoteService.getTravelNoteById(requestDto.getTravelNoteId()),
                requestDto.getAdult(),
                requestDto.getChild(),
                requestDto.getAnimal());
    }

    @PatchMapping("/public-share")
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

    @PatchMapping("/companion")
    public void addTravelMakingNoteCompanion(
            @RequestBody @Valid ContentRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        travelNoteService.addNoteCompanion(
                travelNoteService.getTravelNoteById(requestDto.getId()), member, requestDto.getContent());
    }

    @DeleteMapping("/companion/{travelNoteId}/{memberId}")
    public void deleteTravelMakingNoteCompanion(
            @PathVariable(name = "travelNoteId") Long travelNoteId,
            @PathVariable(name = "memberId") Long memberId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        travelNoteService.deleteCompanion(
                travelNoteService.getTravelNoteById(travelNoteId), member, memberId);
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

    @PostMapping("/comment")
    public void addCourseComment(
            @RequestBody @Valid CourseCommentRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        commentService.addCourseComment(
                travelNoteService.getTravelNoteById(requestDto.getTravelNoteId()),
                member,
                requestDto.getDay(),
                requestDto.getText());
    }

    @DeleteMapping("/comment/{commentId}")
    public void deleteCourseComment(
            @PathVariable(name = "commentId") Long commentId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        commentService.deleteCourseComment(member, commentId);
    }

    @GetMapping("/week")
    public Result<List<TravelNoteItemDto>> callWeekTravelNote(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return new Result<>(travelNoteService.getWeekNotes(MainPageConst.NUMBER_OF_WEEK_NOTES, member));
    }

    @GetMapping("/like/{travelNoteId}")
    public LikeItemDto callTravelNoteLike(
            @PathVariable(name = "travelNoteId") Long travelNoteId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        return travelNoteLikeService.getLikeInfo(
                travelNoteService.getTravelNoteById(travelNoteId), member);
    }

    @PatchMapping("/like")
    public void changeTravelNoteLike(
            @RequestBody @Valid LikeRequestDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        travelNoteLikeService.changeTravelNoteLike(member, requestDto.getId(), requestDto.isLike());
    }

//    @PostMapping("/place/add")
//    public void addRecommendedPlaceToCourse(
//            @RequestBody @Valid RecommendPlaceRequestDto requestDto) {
//        TravelNote travelNote = travelNoteService.getTravelNoteById(requestDto.getTravelNoteId());
//
//        if (requestDto.getPlaceId() != null) {
//            courseService.addPlace(travelNote, requestDto.getDay(), requestDto.getPlaceId());
//        } else {
//            courseService.addPlaces(travelNote, requestDto.getDay(), requestDto.getPlaceIdList());
//        }
//    }

//    @GetMapping("/place/recommend/{travelNoteId}")
//    public Result<List<PlaceResponseDto>> refreshPlaceRecommended(
//            @PathVariable("travelNoteId") Long travelNoteId) {
//        List<Places> placeList = recommendService.getRecommendedPlacesByTravelNote(
//                travelNoteService.getTravelNoteById(travelNoteId), RecommendConst.NOTE_PLACE_RECOMMEND_NUM);
//
//        return new Result<>(placeList.stream().map(PlaceResponseDto::new).toList());
//    }

//    @GetMapping("/course/{travelNoteId}")
//    public Result<List<TravelMakingNoteCourseResponseDto>> callTravelMakingNoteCourse(
//            @PathVariable("travelNoteId") Long travelNoteId) {
//        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);
//
//        List<Course> courseList = courseService.getCoursesByTravelNote(travelNote);
//        List<RouteInfoDto> routeInfoList = courseService.getRouteInfosByTravelNote(travelNote);
//
//        List<TravelMakingNoteCourseResponseDto> response = new ArrayList<>();
//        int indexStart = 0;
//        for (int i = 0; i < courseList.size(); i++) {
//            RouteInfoDto routeInfoDto = routeInfoList.get(i);
//
//            Course course = courseList.get(i);
//            List<Places> placeList = placeService.getPlacesByCourse(course);
//
//            response.add(new TravelMakingNoteCourseResponseDto(
//                    indexStart,
//                    course.getDay(),
//                    placeList,
//                    routeInfoDto.getRouteInfos()));
//            indexStart += placeList.size();
//        }
//
//        return new Result<>(response);
//    }
//
//    @GetMapping("/course/coordinate/{travelNoteId}")
//    public Result<List<CourseCoordinateDto>> callTravelMakingNoteCourseCoordinate(
//            @PathVariable("travelNoteId") Long travelNoteId) {
//        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);
//
//        List<Course> courseList = courseService.getCoursesByTravelNote(travelNote);
//        List<String> markerColorList = mapMarkerService.getMarkerColors(courseList.size());
//
//        List<CourseCoordinateDto> response = new ArrayList<>();
//        for (Course course : courseList) {
//            response.add(new CourseCoordinateDto(
//                    course.getDay(),
//                    markerColorList.get(course.getDay() - 1),
//                    placeService.getPlacesByCourse(course)));
//        }
//
//        return new Result<>(response);
//    }
//
//    @PostMapping("/course/save/{travelNoteId}")
//    public void saveTravelMakingNoteCourse(
//            @PathVariable("travelNoteId") Long travelNoteId,
//            @RequestBody @Valid List<List<Long>> request) {
//        travelNoteService.updateCourse(travelNoteService.getTravelNoteById(travelNoteId), request);
//    }
//
//    @GetMapping("/course/optimize/{travelNoteId}")
//    public void optimizeCourse(
//            @PathVariable("travelNoteId") Long travelNoteId) {
//        courseService.optimizeCourse(travelNoteService.getTravelNoteById(travelNoteId));
//    }
//
//    @GetMapping("/course/route/{travelNoteId}")
//    public Result<List<RouteInfoDto>> callRoutes(
//            @PathVariable("travelNoteId") Long travelNoteId) {
//        return new Result<>(
//                courseService.getRouteInfosByTravelNote(travelNoteService.getTravelNoteById(travelNoteId)));
//    }

}
