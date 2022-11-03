package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.*;
import com.yeoreodigm.server.dto.ContentRequestDto;
import com.yeoreodigm.server.dto.CountDto;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.comment.CommentItemDto;
import com.yeoreodigm.server.dto.comment.CourseCommentRequestDto;
import com.yeoreodigm.server.dto.constraint.MainPageConst;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.like.LikeRequestDto;
import com.yeoreodigm.server.dto.member.MemberEmailItemDto;
import com.yeoreodigm.server.dto.travelnote.*;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

import static com.yeoreodigm.server.dto.constraint.SearchConst.SEARCH_OPTION_LIKE_DESC;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/note")
@Tag(name = "Travel Note", description = "여행 노트 API")
public class TravelNoteApiController {

    private final TravelNoteService travelNoteService;

    private final CourseCommentService commentService;

    private final MemberService memberService;

    private final CourseService courseService;

    private final PlaceService placeService;

    private final RecommendService recommendService;

    @PostMapping("/new")
    @Operation(summary = "새 여행 노트 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public TravelNoteIdDto createNewTravelNote(
            Authentication authentication,
            @RequestBody @Valid NewTravelNoteRequestDto requestDto) {
        TravelNote travelNote = travelNoteService.createTravelNote(
                memberService.getMemberByAuth(authentication), requestDto, placeService.getRandomImageUrl());

        List<List<Long>> recommendCourseList = recommendService.getRecommendedCourses(travelNote);

        if (recommendCourseList == null) throw new BadRequestException("코스 생성 중 에러가 발생하였습니다.");

        courseService.saveNewCoursesByRecommend(travelNote, recommendCourseList);
        courseService.optimizeCourse(travelNote);

        return new TravelNoteIdDto(travelNote.getId());
    }

    @GetMapping("/info/{travelNoteId}")
    @Operation(summary = "기본 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public TravelNoteInfoDto callTravelMakingNoteInfo(
            Authentication authentication,
            @PathVariable("travelNoteId") Long travelNoteId) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);

        NoteAuthority noteAuthority = travelNoteService.checkNoteAuthority(
                memberService.getMemberByAuth(authentication), travelNote);

        travelNoteService.updateModified(travelNote);

        return new TravelNoteInfoDto(noteAuthority, travelNote);
    }

    @PatchMapping("/title")
    @Operation(summary = "노트 이름 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void changeTravelMakingNoteTitle(
            @RequestBody @Valid NoteTitleRequestDto requestDto) {
        travelNoteService.changeTitle(
                travelNoteService.getTravelNoteById(requestDto.getTravelNoteId()), requestDto.getNewTitle());
    }

    @PatchMapping("/composition")
    @Operation(summary = "인원 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void changeTravelMakingNoteComposition(
            @RequestBody @Valid NoteCompositionRequestDto requestDto) {
        travelNoteService.changeComposition(
                travelNoteService.getTravelNoteById(requestDto.getTravelNoteId()),
                requestDto.getAdult(),
                requestDto.getChild(),
                requestDto.getAnimal());
    }

    @PatchMapping("/public-share")
    @Operation(summary = "동선 공개 여부 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void changeTravelMakingNotePublicShare(
            @RequestBody @Valid PublicShareRequestDto requestDto) {
        travelNoteService.changePublicShare(
                travelNoteService.getTravelNoteById(requestDto.getTravelNoteId()), requestDto.isPublicShare());
    }

    @GetMapping("/companion/{travelNoteId}")
    @Operation(summary = "동행자 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public Result<List<MemberEmailItemDto>> callTravelMakingNoteCompanion(
            @PathVariable("travelNoteId") Long travelNoteId) {

        return new Result<>(travelNoteService.getCompanionMember(travelNoteService.getTravelNoteById(travelNoteId))
                .stream()
                .map(MemberEmailItemDto::new)
                .toList());
    }

    @PatchMapping("/companion")
    @Operation(summary = "동행자 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void addTravelMakingNoteCompanion(
            Authentication authentication,
            @RequestBody @Valid ContentRequestDto requestDto) {
        travelNoteService.addNoteCompanion(
                travelNoteService.getTravelNoteById(requestDto.getId()),
                memberService.getMemberByAuth(authentication),
                memberService.searchMember(requestDto.getContent()));
    }

    @DeleteMapping("/companion/{travelNoteId}/{memberId}")
    @Operation(summary = "동행자 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void deleteTravelMakingNoteCompanion(
            Authentication authentication,
            @PathVariable(name = "travelNoteId") Long travelNoteId,
            @PathVariable(name = "memberId") Long memberId) {
        travelNoteService.deleteCompanion(
                travelNoteService.getTravelNoteById(travelNoteId),
                memberService.getMemberByAuth(authentication),
                memberId);
    }

    @GetMapping("/comment/{travelNoteId}/{day}")
    @Operation(summary = "댓글 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public Result<List<CommentItemDto>> callCourseComment(
            Authentication authentication,
            @PathVariable(name = "travelNoteId") Long travelNoteId,
            @PathVariable(name = "day") int day) {

        return new Result<>(commentService.getCourseCommentsByTravelNoteAndDay(
                travelNoteService.getTravelNoteById(travelNoteId), day)
                .stream()
                .map(courseComment -> new CommentItemDto(
                        courseComment, memberService.getMemberByAuth(authentication)))
                .toList());
    }

    @PostMapping("/comment")
    @Operation(summary = "댓글 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void addCourseComment(
            Authentication authentication,
            @RequestBody @Valid CourseCommentRequestDto requestDto) {
        commentService.addCourseComment(
                travelNoteService.getTravelNoteById(requestDto.getTravelNoteId()),
                memberService.getMemberByAuth(authentication),
                requestDto.getDay(),
                requestDto.getText());
    }

    @DeleteMapping("/comment/{commentId}")
    @Operation(summary = "댓글 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void deleteCourseComment(
            Authentication authentication,
            @PathVariable(name = "commentId") Long commentId) {
        commentService.deleteCourseComment(memberService.getMemberByAuth(authentication), commentId);
    }

    @GetMapping("/week")
    @Operation(summary = "주간 인기 여행 노트 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public Result<List<TravelNoteLikeDto>> callWeekTravelNote(Authentication authentication) {
        return new Result<>(travelNoteService.getWeekNotes(
                MainPageConst.NUMBER_OF_WEEK_NOTES, memberService.getMemberByAuth(authentication)));
    }

    @GetMapping("/like/{travelNoteId}")
    @Operation(summary = "좋아요 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public LikeItemDto callTravelNoteLike(
            Authentication authentication,
            @PathVariable(name = "travelNoteId") Long travelNoteId) {
        return travelNoteService.getLikeInfo(
                travelNoteService.getTravelNoteById(travelNoteId),
                memberService.getMemberByAuth(authentication));
    }

    @PatchMapping("/like")
    @Operation(summary = "좋아요 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public void changeTravelNoteLike(
            Authentication authentication,
            @RequestBody @Valid LikeRequestDto requestDto) {
        travelNoteService.changeTravelNoteLike(
                memberService.getMemberByAuth(authentication), requestDto.getId(), requestDto.isLike());
    }

    @GetMapping("/like/list/{page}/{limit}")
    @Operation(summary = "좋아요 누른 여행 노트 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public PageResult<List<PublicTravelNoteDto>> callTravelNoteLikeList(
            Authentication authentication,
            @PathVariable("page") int page,
            @PathVariable("limit") int limit) {
        Member member = memberService.getMemberByAuth(authentication);

        return new PageResult<>(
                travelNoteService.getNotesByNoteLikes(travelNoteService.getNoteLikes(member, page, limit))
                        .stream()
                        .map(travelNote -> travelNoteService.getPublicTravelNoteDto(travelNote, member))
                        .toList(),
                travelNoteService.checkNextNoteLikePage(member, page, limit));
    }

    @GetMapping("/like/list")
    @Operation(summary = "좋아요 누른 여행 노트 조회 v2")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public PageResult<List<PublicTravelNoteDto>> callTravelNoteLikeListV2(
            Authentication authentication,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit,
            @RequestParam(value = "option", required = false, defaultValue = "0") int option) {
        Member member = memberService.getMemberByAuth(authentication);

        if (Objects.equals(SEARCH_OPTION_LIKE_DESC, option)) {
            return new PageResult<>(
                    travelNoteService.getTravelNotesOrderByLike(member, page, limit)
                            .stream()
                            .map(travelNote -> travelNoteService.getPublicTravelNoteDto(travelNote, member))
                            .toList(),
                    travelNoteService.checkNextNoteLikePage(member, page, limit));
        }

        return new PageResult<>(
                travelNoteService.getNotesByNoteLikes(travelNoteService.getNoteLikes(member, page, limit))
                        .stream()
                        .map(travelNote -> travelNoteService.getPublicTravelNoteDto(travelNote, member))
                        .toList(),
                travelNoteService.checkNextNoteLikePage(member, page, limit));
    }

    @GetMapping("/like/list/{memberId}/{page}/{limit}")
    @Operation(summary = "좋아요 누른 여행 노트 조회 (멤버 상세 페이지)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public PageResult<List<PublicTravelNoteDto>> callMemberTravelNoteLikeList(
            Authentication authentication,
            @PathVariable("memberId") Long memberId,
            @PathVariable("page") int page,
            @PathVariable("limit") int limit) {
        Member targetMember = memberService.getMemberById(memberId);

        List<TravelNoteLike> noteLikeList = travelNoteService.getNoteLikes(targetMember, page, limit);

        List<TravelNote> travelNoteList = travelNoteService.getNotesByNoteLikes(noteLikeList);

        int next = travelNoteService.checkNextNoteLikePage(targetMember, page, limit);

        return new PageResult<>(
                travelNoteList
                        .stream()
                        .map(travelNote -> travelNoteService.getPublicTravelNoteDto(
                                travelNote, memberService.getMemberByAuth(authentication)))
                        .toList(),
                next);
    }

    @GetMapping("/my/{page}/{limit}")
    @Operation(summary = "내 여행 노트 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public PageResult<List<MyTravelNoteDto>> callMyTravelNotes(
            Authentication authentication,
            @PathVariable("page") int page,
            @PathVariable("limit") int limit) {
        Member member = memberService.getMemberByAuth(authentication);

        return new PageResult<>(
                travelNoteService.getMyTravelNoteDtoList(travelNoteService.getMyTravelNote(member, page, limit)),
                travelNoteService.checkNextMyTravelNote(member, page, limit));
    }

    @GetMapping("/my/count")
    @Operation(summary = "내 여행 노트 개수 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public CountDto callMyTravelNoteCount(Authentication authentication) {
        return new CountDto(travelNoteService.getMyTravelNoteCount(memberService.getMemberByAuth(authentication)));
    }

    @GetMapping("/my/board/{page}/{limit}")
    @Operation(summary = "내 여행 노트 조회 (여행 피드 작성 페이지)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public PageResult<List<MyTravelNoteBoardDto>> callMyTravelNoteBoard(
            Authentication authentication,
            @PathVariable("page") int page,
            @PathVariable("limit") int limit) {
        Member member = memberService.getMemberByAuth(authentication);

        return new PageResult<>(
                travelNoteService.getMyTravelNote(member, page, limit)
                        .stream()
                        .map(travelNote -> new MyTravelNoteBoardDto(travelNote, courseService.countAllPlace(travelNote)))
                        .toList(),
                travelNoteService.checkNextMyTravelNote(member, page, limit));
    }

    @GetMapping("/board/{travelNoteId}")
    @Operation(summary = "선택한 여행 노트 조회 (여행 피드 작성 페이지)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN", content = @Content(schema = @Schema(hidden = true)))
    })
    public MyTravelNoteBoardDto callTravelNoteBoard(
            @PathVariable("travelNoteId") Long travelNoteId) {
        TravelNote travelNote = travelNoteService.getTravelNoteById(travelNoteId);
        return new MyTravelNoteBoardDto(travelNote, courseService.countAllPlace(travelNote));
    }

    @GetMapping("/public/{memberId}/{page}/{limit}")
    @Operation(summary = "사용자 여행 노트 조회 (멤버 상세 페이지)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public PageResult<List<PublicTravelNoteDto>> callMyPublicTravelNotes(
            Authentication authentication,
            @PathVariable("memberId") Long memberId,
            @PathVariable("page") int page,
            @PathVariable("limit") int limit) {
        Member targetMember = memberService.getMemberById(memberId);

        List<TravelNote> travelNoteList = travelNoteService.getPublicNotes(targetMember, page, limit);

        return new PageResult<>(
                travelNoteList
                        .stream()
                        .map(travelNote -> travelNoteService.getPublicTravelNoteDto(
                                travelNote, memberService.getMemberByAuth(authentication)))
                        .toList(),
                travelNoteService.checkNextPublicMyNote(targetMember, page, limit));
    }

    @GetMapping("/all")
    @Operation(summary = "모든 여행 노트 ID 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public Result<List<TravelNoteStringIdDto>> callAllTravelNoteId() {
        return new Result<>(travelNoteService.getAll()
                .stream()
                .map(TravelNoteStringIdDto::new)
                .toList());
    }

    @GetMapping("/all/count")
    @Operation(summary = "모든 여행 노트 개수 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content(schema = @Schema(hidden = true)))
    })
    public TravelNoteCountDto callAllTravelNoteCount() {
        return new TravelNoteCountDto(travelNoteService.countAll());
    }

}
