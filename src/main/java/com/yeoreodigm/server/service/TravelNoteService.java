package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.*;
import com.yeoreodigm.server.dto.constraint.EmailConst;
import com.yeoreodigm.server.dto.constraint.TravelNoteConst;
import com.yeoreodigm.server.dto.detail.TravelNoteAndLikeDto;
import com.yeoreodigm.server.dto.detail.TravelNoteDetailInfo;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.mainpage.MainPageTravelNote;
import com.yeoreodigm.server.dto.noteprepare.NewTravelNoteDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.CourseRepository;
import com.yeoreodigm.server.repository.LogRepository;
import com.yeoreodigm.server.repository.MemberRepository;
import com.yeoreodigm.server.repository.TravelNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TravelNoteService {

    private final TravelNoteRepository travelNoteRepository;

    private final CourseRepository courseRepository;

    private final MemberRepository memberRepository;

    private final MemberService memberService;

    private final RecommendService recommendService;

    private final CourseService courseService;

    private final LogRepository logRepository;

    private final TravelNoteLikeService travelNoteLikeService;

    private final PlaceService placeService;

    private final static int RANDOM_NOTE_NUMBER = 300;

    public TravelNote findTravelNote(Long travelNoteId) {
        TravelNote travelNote = travelNoteRepository.findById(travelNoteId);
        if (travelNote != null) {
            return travelNote;
        } else {
            throw new BadRequestException("일치하는 여행 메이킹 노트가 없습니다.");
        }
    }

    public TravelNote createTravelNote(Member member, NewTravelNoteDto requestDto) {
        if (member == null) throw new BadRequestException("로그인이 필요합니다.");

        String title = member.getNickname() +
                "님의 " +
                TravelNoteConst.TITLE_LIST[(int) (Math.random() * TravelNoteConst.TITLE_LIST.length)] +
                " 제주여행";

        return TravelNote.builder()
                .member(member)
                .title(title)
                .dayStart(requestDto.getDayStart())
                .dayEnd(requestDto.getDayEnd())
                .adult(requestDto.getAdult())
                .child(requestDto.getChild())
                .animal(requestDto.getAnimal())
                .region(requestDto.getRegion())
                .theme(requestDto.getTheme())
                .placesInput(requestDto.getPlaces())
                .publicShare(TravelNoteConst.PUBLIC_SHARE_DEFAULT_VALUE)
                .thumbnail(placeService.getRandomImageUrl())
                .build();
    }

    @Transactional
    public Long submitNotePrepare(TravelNote travelNote) {

        travelNoteRepository.saveAndFlush(travelNote);

        List<List<Long>> recommendedCourseList = recommendService.getRecommendedCourses(
                travelNote.getMember(),
                travelNote.getDayStart(),
                travelNote.getDayEnd(),
                travelNote.getPlacesInput(),
                travelNote.getRegion());

        if (recommendedCourseList != null) {
            for (int idx = 0; idx < recommendedCourseList.size(); idx++) {
                courseService.saveCourse(travelNote, idx + 1, recommendedCourseList.get(idx));
            }

            return travelNote.getId();
        } else {
            throw new BadRequestException("코스 생성 중 에러가 발생하였습니다.");
        }

    }

    public NoteAuthority checkNoteAuthority(Member member, TravelNote travelNote) {
        if (member == null) {
            return NoteAuthority.ROLE_VISITOR;
        }

        if (Objects.equals(travelNote.getMember().getId(), member.getId())) {
            return NoteAuthority.ROLE_OWNER;
        } else {
            return NoteAuthority.ROLE_COMPANION;
        }
        //동행자 추가하면 ROLE_COMPANION 확인하도록 수정
    }

    @Transactional
    public void updateNoteCourse(Long travelNoteId, List<List<Long>> courseListNew) {
        TravelNote travelNote = findTravelNote(travelNoteId);

        List<Course> courseListOld = travelNote.getCourses();

        for (Course courseOld : courseListOld) {
            int day = courseOld.getDay();
            if (day <= courseListNew.size()) {
                courseOld.changePlaces(courseListNew.get(day - 1));
                courseRepository.save(courseOld);
            } else {
                courseRepository.deleteByCourse(courseOld);
            }
        }
        if (courseListNew.size() > courseListOld.size()) {
            for (int i = courseListOld.size(); i < courseListNew.size(); i++) {
                courseRepository.save(new Course(travelNote, i + 1, courseListNew.get(i)));
            }
        }
        courseRepository.flush();
    }

    @Transactional
    public void changeTitle(Long travelNoteId, String newTitle) {
        if (newTitle.length() > 30) {
            throw new BadRequestException("여행 메이킹 노트의 이름은 30자 이하만 가능합니다.");
        }

        TravelNote travelNote = findTravelNote(travelNoteId);
        travelNote.changeTitle(newTitle);

        travelNoteRepository.saveAndFlush(travelNote);
    }

    @Transactional
    public void changeComposition(Long travelNoteId, int adult, int child, int animal) {
        if (adult < 0 || child < 0 || animal < 0) {
            throw new BadRequestException("여행 인원을 확인해주시기 바랍니다.");
        }

        TravelNote travelNote = findTravelNote(travelNoteId);
        travelNote.changeComposition(adult, child, animal);
        travelNoteRepository.saveAndFlush(travelNote);
    }

    @Transactional
    public void changePublicShare(Long travelNoteId, boolean publicShare) {
        TravelNote travelNote = findTravelNote(travelNoteId);
        travelNote.changePublicShare(publicShare);
        travelNoteRepository.saveAndFlush(travelNote);
    }

    @Transactional
    public Member addNoteCompanion(Long travelNoteId, String content) {

        Member member = memberService.searchMember(content);
        TravelNote travelNote = findTravelNote(travelNoteId);
        if (member == null) {
            throw new BadRequestException("일치하는 사용자가 없습니다.");
        }

        if (travelNote.getMember().getId().equals(member.getId())) {
            throw new BadRequestException("여행 메이킹 노트의 소유자입니다.");
        }

        List<Long> companion = travelNote.getCompanion();
        if (companion.contains(member.getId())) {
            throw new BadRequestException("이미 추가된 사용자입니다.");
        } else {
            companion.add(member.getId());
            travelNote.changeCompanion(companion);
            travelNoteRepository.saveAndFlush(travelNote);
            return member;
        }

    }

    @Transactional
    public void deleteCompanion(Long travelNoteId, Long memberId) {

        TravelNote travelNote = findTravelNote(travelNoteId);
        List<Long> companion = travelNote.getCompanion();

        companion.remove(memberId);
        travelNote.changeCompanion(companion);
        travelNoteRepository.saveAndFlush(travelNote);

    }

    public List<Member> findCompanion(Long travelNoteId) {

        TravelNote travelNote = findTravelNote(travelNoteId);

        return travelNote
                .getCompanion()
                .stream()
                .map(memberRepository::findById)
                .toList();

    }

    public List<MainPageTravelNote> getRecommendedNotes(int limit) {
        List<TravelNote> travelNoteList = travelNoteRepository.findByPublicLimiting(limit);

        return getMainPageItemList(travelNoteList);
    }

    public List<MainPageTravelNote> getRandomNotes(int limit) {
        List<TravelNote> travelNoteList = travelNoteRepository.findByPublicLimiting(RANDOM_NOTE_NUMBER);
        int index = (int) (Math.random() * travelNoteList.size());

        List<TravelNote> result = new ArrayList<>();

        for (int i = 0; i < limit; i++) {
            result.add(travelNoteList.get(index));
            index += 1;
            while (index >= travelNoteList.size()) {
                index -= travelNoteList.size();
            }
        }

        return getMainPageItemList(result);
    }

    public List<MainPageTravelNote> getWeeklyNotes(int limit) {
        List<TravelNote> travelNoteList = logRepository
                .findMostNoteIdLimiting(limit)
                .stream()
                .map(travelNoteRepository::findById)
                .toList();

        return getMainPageItemList(travelNoteList);
    }

    private List<MainPageTravelNote> getMainPageItemList(List<TravelNote> travelNoteList) {
        return travelNoteList.stream().map(MainPageTravelNote::new).toList();
    }

    public TravelNoteDetailInfo getTravelNoteInfo(TravelNote travelNote) {
        long between = ChronoUnit.DAYS.between(travelNote.getDayStart(), travelNote.getDayEnd());

        StringBuilder period = new StringBuilder();
        if (between == 0) {
            period.append("당일치기");
        } else if (between >= 13 && between <= 15) {
            period.append("보름살기 (").append(between).append("박 ").append(between + 1).append("일)");
        } else if (between >= 27 && between <= 32) {
            period.append("한달살기 (").append(between).append("박 ").append(between + 1).append("일)");
        } else {
            period.append(between).append("박 ").append(between + 1).append("일");
        }

        List<String> theme = new ArrayList<>();
        if (travelNote.getChild() > 0) {
            theme.add("아이와 함께");
        } else if (travelNote.getAnimal() > 0) {
            theme.add("반려동물과 함께");
        }
        theme.addAll(travelNote.getTheme());

        return new TravelNoteDetailInfo(
                travelNote.getTitle(),
                period.toString(),
                travelNote.getRegion(),
                theme);
    }

    public List<TravelNoteAndLikeDto> getTempTravelNoteList(int limit, Member member) {
        List<TravelNote> travelNoteList = travelNoteRepository.findByPublicLimiting(limit);
        List<LikeItemDto> likeItemDtoList = travelNoteList
                .stream()
                .map(travelNote -> travelNoteLikeService.getLikeInfo(travelNote.getId(), member.getId()))
                .toList();

        List<TravelNoteAndLikeDto> result = new ArrayList<>();
        for (int i = 0; i < travelNoteList.size(); i++) {
            result.add(new TravelNoteAndLikeDto(travelNoteList.get(i), likeItemDtoList.get(i)));
        }
        return result;
    }

}