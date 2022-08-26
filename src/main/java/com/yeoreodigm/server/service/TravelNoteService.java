package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.*;
import com.yeoreodigm.server.dto.detail.travelnote.TravelNoteDetailInfo;
import com.yeoreodigm.server.dto.mainpage.MainPageTravelNote;
import com.yeoreodigm.server.dto.noteprepare.NewTravelNoteRequestDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.CourseRepository;
import com.yeoreodigm.server.repository.LogRepository;
import com.yeoreodigm.server.repository.MemberRepository;
import com.yeoreodigm.server.repository.TravelNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.yeoreodigm.server.dto.constraint.TravelNoteConst.*;


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

    public TravelNote getTravelNoteById(Long travelNoteId) {
        TravelNote travelNote = travelNoteRepository.findById(travelNoteId);
        if (travelNote != null) {
            return travelNote;
        } else {
            throw new BadRequestException("일치하는 여행 노트가 없습니다.");
        }
    }

    public TravelNote createTravelNote(Member member, NewTravelNoteRequestDto requestDto) {
        if (member == null) throw new BadRequestException("로그인이 필요합니다.");

        String title = member.getNickname() +
                "님의 " +
                TITLE_LIST[(int) (Math.random() * TITLE_LIST.length)] +
                " 제주여행";

        return TravelNote.builder()
                .id(getRandomId())
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
                .publicShare(PUBLIC_SHARE_DEFAULT_VALUE)
                .thumbnail(placeService.getRandomImageUrl())
                .build();
    }

    public TravelNote createTravelNoteFromOther(TravelNote originTravelNote, Member member) {
        if (member == null) throw new BadRequestException("로그인이 필요합니다.");

        String title = member.getNickname() +
                "님의 " +
                TITLE_LIST[(int) (Math.random() * TITLE_LIST.length)] +
                " 제주여행";

        long between = ChronoUnit.DAYS.between(originTravelNote.getDayStart(), originTravelNote.getDayEnd());
        LocalDate dayStart = LocalDate.now();
        LocalDate dayEnd = dayStart.plusDays(between);

        int birthYear = member.getBirth().getYear();
        int adult = 0;
        int child = 0;
        if (LocalDate.now().getYear() - birthYear > 18) {
            adult++;
        } else {
            child++;
        }

        return TravelNote.builder()
                .id(getRandomId())
                .member(member)
                .title(title)
                .dayStart(dayStart)
                .dayEnd(dayEnd)
                .adult(adult)
                .child(child)
                .region(originTravelNote.getRegion())
                .theme(originTravelNote.getTheme())
                .placesInput(new ArrayList<>())
                .publicShare(PUBLIC_SHARE_DEFAULT_VALUE)
                .thumbnail(placeService.getRandomImageUrl())
                .build();
    }

    private long getRandomId() {
        long id = Long.parseLong(Integer.toString((int) (Math.random() * (ID_SIZE_MAX - ID_SIZE_MIN) + ID_SIZE_MIN))
                + (int) (Math.random() * (ID_SIZE_MAX - ID_SIZE_MIN) + ID_SIZE_MIN));

        TravelNote travelNote = travelNoteRepository.findById(id);
        while (travelNote != null) {
            id = Long.parseLong(Integer.toString((int) (Math.random() * (ID_SIZE_MAX - ID_SIZE_MIN) + ID_SIZE_MIN))
                    + (int) (Math.random() * (ID_SIZE_MAX - ID_SIZE_MIN) + ID_SIZE_MIN));

            travelNote = travelNoteRepository.findById(id);
        }
        return id;
    }

    @Transactional
    public Long submitNotePrepare(TravelNote travelNote) {
        travelNoteRepository.saveAndFlush(travelNote);

        List<List<Long>> recommendedCourseList = recommendService.getRecommendedCourses(travelNote);

        if (recommendedCourseList != null) {
            courseService.saveNewCoursesByRecommend(travelNote, recommendedCourseList);

            return travelNote.getId();
        } else {
            throw new BadRequestException("코스 생성 중 에러가 발생하였습니다.");
        }
    }

    @Transactional
    public Long submitFromOtherNote(TravelNote originTravelNote, TravelNote travelNote) {
        travelNoteRepository.saveAndFlush(travelNote);

        List<Course> courseList = courseService.getCoursesByTravelNote(originTravelNote);

        for (Course course : courseList) {
            courseService.saveNewCourse(travelNote, course.getDay(), course.getPlaces());
        }

        return travelNote.getId();
    }

    public NoteAuthority checkNoteAuthority(Member member, TravelNote travelNote) {
        if (member == null) throw new BadRequestException("로그인이 필요합니다.");

        Long memberId = member.getId();
        if (memberId.equals(travelNote.getMember().getId())) {
            return NoteAuthority.ROLE_OWNER;
        } else if (travelNote.getCompanion().contains(memberId)) {
            return NoteAuthority.ROLE_COMPANION;
        } else {
            throw new BadRequestException("여행 메이킹 노트에 접근 권한이 없습니다.");
        }
    }

    @Transactional
    public void updateCourse(TravelNote travelNote, List<List<Long>> courseListNew) {
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
    public void changeTitle(TravelNote travelNote, String newTitle) {
        if (newTitle.length() > 30) {
            throw new BadRequestException("여행 메이킹 노트의 이름은 30자 이하만 가능합니다.");
        }

        travelNote.changeTitle(newTitle);
        travelNoteRepository.saveAndFlush(travelNote);
    }

    @Transactional
    public void changeComposition(TravelNote travelNote, int adult, int child, int animal) {
        if (adult < 0 || child < 0 || animal < 0 || (adult == 0 && child == 0)) {
            throw new BadRequestException("여행 인원을 확인해주시기 바랍니다.");
        }

        travelNote.changeComposition(adult, child, animal);
        travelNoteRepository.saveAndFlush(travelNote);
    }

    @Transactional
    public void changePublicShare(TravelNote travelNote, boolean publicShare) {
        travelNote.changePublicShare(publicShare);
        travelNoteRepository.saveAndFlush(travelNote);
    }

    @Transactional
    public void addNoteCompanion(TravelNote travelNote, Member member, String content) {
        if (member == null || !member.getId().equals(travelNote.getMember().getId())) {
            throw new BadRequestException("여행 메이킹 노트 소유자만 동행자를 추가할 수 있습니다.");
        }

        Member newCompanion = memberService.searchMember(content);
        if (newCompanion == null) {
            throw new BadRequestException("일치하는 사용자가 없습니다.");
        }

        if (newCompanion.getId().equals(travelNote.getMember().getId())) {
            throw new BadRequestException("여행 메이킹 노트의 소유자입니다.");
        }

        List<Long> companionList = travelNote.getCompanion();
        if (companionList.contains(newCompanion.getId())) {
            throw new BadRequestException("이미 추가된 사용자입니다.");
        } else {
            companionList.add(newCompanion.getId());
            travelNote.changeCompanion(companionList);
            travelNoteRepository.saveAndFlush(travelNote);
        }
    }

    @Transactional
    public void deleteCompanion(TravelNote travelNote, Member member, Long companionId) {
        if (member == null || !member.getId().equals(travelNote.getMember().getId())) {
            throw new BadRequestException("여행 메이킹 노트 소유자만 동행자를 추가할 수 있습니다.");
        }

        List<Long> companion = travelNote.getCompanion();

        companion.remove(companionId);
        travelNote.changeCompanion(companion);
        travelNoteRepository.saveAndFlush(travelNote);
    }

    public List<Member> getCompanionMember(TravelNote travelNote) {
        return travelNote
                .getCompanion()
                .stream()
                .map(memberRepository::findById)
                .toList();
    }

    public List<MainPageTravelNote> getRecommendedNotesMainPage(int limit, Member member) {
        List<TravelNote> travelNoteList = recommendService.getRecommendedNotes(limit, member);
        return getMainPageItemList(travelNoteList, member);
    }

    public List<MainPageTravelNote> getRandomNotesMainPage(int limit, Member member) {
        return getMainPageItemList(getRandomNotes(limit), member);
    }

    public List<TravelNote> getRandomNotes(int limit) {
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
        return result;
    }

    public List<MainPageTravelNote> getWeeklyNotesMainPage(int limit, Member member) {
        List<TravelNote> travelNoteList = logRepository
                .findMostNoteIdLimiting(limit)
                .stream()
                .map(travelNoteRepository::findById)
                .toList();

        return getMainPageItemList(travelNoteList, member);
    }

    private List<MainPageTravelNote> getMainPageItemList(List<TravelNote> travelNoteList, Member member) {
        List<MainPageTravelNote> result = new ArrayList<>();
        for (TravelNote travelNote : travelNoteList) {
            result.add(new MainPageTravelNote(travelNote, travelNoteLikeService.getLikeInfo(travelNote, member)));
        }
        return result;
    }

    public TravelNoteDetailInfo getTravelNoteDetailInfo(TravelNote travelNote) {
        if (!travelNote.isPublicShare()) throw new BadRequestException("이 여행 메이킹 노트는 볼 수 없습니다.");

        StringBuilder period = getPeriod(travelNote);

        List<String> theme = getThemeFromComposition(travelNote);
        theme.addAll(travelNote.getTheme());

        return new TravelNoteDetailInfo(
                travelNote.getTitle(),
                period.toString(),
                travelNote.getRegion(),
                theme,
                travelNote.getThumbnail());
    }

    private List<String> getThemeFromComposition(TravelNote travelNote) {
        List<String> theme = new ArrayList<>();
        if (travelNote.getChild() > 0) {
            theme.add("아이와 함께");
        } else if (travelNote.getAnimal() > 0) {
            theme.add("반려동물과 함께");
        }
        return theme;
    }

    private StringBuilder getPeriod(TravelNote travelNote) {
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
        return period;
    }

}