package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.*;
import com.yeoreodigm.server.dto.like.LikeItemDto;
import com.yeoreodigm.server.dto.travelnote.*;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.yeoreodigm.server.dto.constraint.TravelNoteConst.*;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TravelNoteService {

    private final TravelNoteRepository travelNoteRepository;

    private final TravelNoteLikeRepository travelNoteLikeRepository;

    private final TravelNoteLogRepository travelNoteLogRepository;

    private final NoteCommentRepository noteCommentRepository;

    private final CourseRepository courseRepository;

    private final MemberRepository memberRepository;

    public List<TravelNote> getAll() {
        return travelNoteRepository.findAll();
    }

    public Long countAll() {
        return travelNoteRepository.countAll();
    }

    public TravelNote getTravelNoteById(Long travelNoteId) {
        TravelNote travelNote = travelNoteRepository.findById(travelNoteId);
        if (travelNote != null) {
            return travelNote;
        } else {
            throw new BadRequestException("일치하는 여행 노트가 없습니다.");
        }
    }

    @Transactional
    public TravelNote createTravelNote(Member member, NewTravelNoteRequestDto requestDto, String thumbnail) {
        if (member == null) throw new BadRequestException("로그인이 필요합니다.");

        long days = ChronoUnit.DAYS.between(requestDto.getDayStart(), requestDto.getDayEnd());
        if (days < 0) {
            throw new BadRequestException("잘못된 일정입니다.");
        } else if (days > 100) {
            throw new BadRequestException("100일 이하의 일정만 생성 가능합니다.");
        }

        String title = getMemberTitle(member);

        TravelNote travelNote = TravelNote.builder()
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
                .thumbnail(thumbnail)
                .build();

        travelNoteRepository.saveAndFlush(travelNote);

        return travelNote;
    }

    @Transactional
    public TravelNote createTravelNoteFromOther(TravelNote originTravelNote, Member member, String thumbnail) {
        if (member == null) throw new BadRequestException("로그인이 필요합니다.");

        String title = getMemberTitle(member);

        long between = ChronoUnit.DAYS.between(originTravelNote.getDayStart(), originTravelNote.getDayEnd());
        LocalDate dayStart = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate dayEnd = dayStart.plusDays(between);

        int birthYear = member.getBirth().getYear();
        int adult = 0;
        int child = 0;
        if (LocalDate.now(ZoneId.of("Asia/Seoul")).getYear() - birthYear > 18) {
            adult++;
        } else {
            child++;
        }

        TravelNote travelNote = TravelNote.builder()
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
                .thumbnail(thumbnail)
                .build();

        travelNoteRepository.saveAndFlush(travelNote);

        return travelNote;
    }

    private String getMemberTitle(Member member) {
        return member.getNickname() + "님의 " + getRandomTitle();
    }

    private String getRandomTitle() {
        return TITLE_LIST[(int) (Math.random() * TITLE_LIST.length)] + " 제주여행";
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
    public void addNoteCompanion(TravelNote travelNote, Member member, Member newCompanion) {
        if (member == null || !member.getId().equals(travelNote.getMember().getId())) {
            throw new BadRequestException("여행 메이킹 노트 소유자만 동행자를 추가할 수 있습니다.");
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

    public List<TravelNoteLikeDto> getWeekNotes(int limit, Member member) {
        List<TravelNote> travelNoteList = travelNoteLogRepository
                .findMostNoteIdLimiting(limit)
                .stream()
                .map(travelNoteRepository::findById)
                .toList();

        return getTravelNoteItemList(travelNoteList, member);
    }

    public List<TravelNoteLikeDto> getTravelNoteItemList(List<TravelNote> travelNoteList, Member member) {
        List<TravelNoteLikeDto> result = new ArrayList<>();
        for (TravelNote travelNote : travelNoteList) {
            result.add(new TravelNoteLikeDto(travelNote, getLikeInfo(travelNote, member)));
        }
        return result;
    }

    public TravelNoteDetailInfo getTravelNoteDetailInfo(TravelNote travelNote) {
        if (!travelNote.isPublicShare()) throw new BadRequestException("이 여행 메이킹 노트는 볼 수 없습니다.");

        String period = getPeriod(travelNote);

        List<String> theme = getThemeFromComposition(travelNote);
        theme.addAll(travelNote.getTheme());

        return new TravelNoteDetailInfo(
                travelNote.getTitle(),
                period,
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

    private String getPeriod(TravelNote travelNote) {
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
        return period.toString();
    }

    @Transactional
    public void updateLog(TravelNote travelNote, Member member) {
        if (member == null) return;

        TravelNoteLog travelNoteLog
                = travelNoteLogRepository.findByTravelNoteIdAndMemberId(travelNote.getId(), member.getId());

        if (travelNoteLog == null) {
            TravelNoteLog newTravelNoteLog = new TravelNoteLog(travelNote, member);
            travelNoteLogRepository.saveAndFlush(newTravelNoteLog);
        } else {
            travelNoteLog.changeVisitTime(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
            travelNoteLogRepository.saveAndFlush(travelNoteLog);
        }
    }

    public Long countTravelNoteLike(TravelNote travelNote) {
        return travelNoteLikeRepository.countByTravelNoteId(travelNote.getId());
    }

    public boolean checkHasLiked(TravelNote travelNote, Member member) {
        if (member == null) return false;
        return travelNoteLikeRepository.findByTravelNoteIdAndMemberId(travelNote.getId(), member.getId()) != null;
    }

    public LikeItemDto getLikeInfo(TravelNote travelNote, Member member) {
        return new LikeItemDto(
                checkHasLiked(travelNote, member),
                countTravelNoteLike(travelNote));
    }

    @Transactional
    public void changeTravelNoteLike(Member member, Long travelNoteId, boolean like) {
        if (member == null) throw new BadRequestException("로그인이 필요합니다.");

        TravelNoteLike travelNoteLike
                = travelNoteLikeRepository.findByTravelNoteIdAndMemberId(travelNoteId, member.getId());

        if (like) {
            if (travelNoteLike == null) {
                TravelNoteLike newTravelNoteLike = new TravelNoteLike(travelNoteId, member.getId());
                travelNoteLikeRepository.saveAndFlush(newTravelNoteLike);
            }
        } else if (travelNoteLike != null) {
            travelNoteLikeRepository.deleteById(travelNoteLike.getId());
        }
    }

    public List<MyTravelNoteDto> getMyTravelNote(Member member, int page, int limit) {
        if (member == null) throw new BadRequestException("로그인이 필요합니다.");
        List<TravelNote> travelNoteList = travelNoteRepository.findByMember(member, limit * (page - 1), limit);

        List<MyTravelNoteDto> result = new ArrayList<>();

        for (TravelNote travelNote : travelNoteList) {
            result.add(new MyTravelNoteDto(travelNote, getPeriod(travelNote)));
        }
        return result;
    }

    public int checkNextMyTravelNote(Member member, int page, int limit) {
        return travelNoteRepository.findByMember(member, page * limit, limit).size() > 0 ? page + 1 : 0;
    }

    public void resetTitle(Member member) {
        List<TravelNote> travelNoteList = travelNoteRepository.findAllByMember(member);

        for (TravelNote travelNote : travelNoteList) {
            travelNote.changeTitle(getRandomTitle());
        }

        travelNoteRepository.flush();
    }

    public List<TravelNoteLike> getNoteLikes(Member member, int page, int limit) {
        if (member == null) return new ArrayList<>();

        return travelNoteLikeRepository
                .findByMemberPaging(member, limit * (page - 1), limit);
    }

    public int checkNextNoteLikePage(Member member, int page, int limit) {
        return getNoteLikes(member, page + 1, limit).size() > 0 ? page + 1 : 0;
    }

    public List<TravelNote> getNotesByNoteLikes(List<TravelNoteLike> noteLikeList) {
        return noteLikeList
                .stream()
                .map(travelNoteLike -> this.getTravelNoteById(travelNoteLike.getTravelNoteId()))
                .toList();
    }

    public List<TravelNote> getPublicNotes(Member member, int page, int limit) {
        return travelNoteRepository.findPublicByMember(member, limit * (page - 1), limit);
    }

    public PublicTravelNoteDto getPublicTravelNoteDto(TravelNote travelNote, Member member) {
        long placeCount = 0L;
        for (Course course : travelNote.getCourses()) {
            placeCount += course.getPlaces().size();
        }

        return new PublicTravelNoteDto(
                travelNote,
                getPeriod(travelNote),
                getLikeInfo(travelNote, member),
                placeCount,
                noteCommentRepository.countByTravelNoteId(travelNote.getId()));
    }

    public int checkNextPublicMyNote(Member member, int page, int limit) {
        return getPublicNotes(member, page + 1, limit).size() > 0 ? page + 1 : 0;
    }

    public List<TravelNote> searchTravelNote(String content, int page, int limit) {
        return travelNoteRepository.findPublicByKeywordPaging(content, limit * (page - 1), limit);
    }

    public int checkNextSearchTravelNote(String content, int page, int limit) {
        return travelNoteRepository.findPublicByKeywordPaging(
                content, limit * page, limit).size() > 0 ? page + 1 : 0;
    }

    @Transactional
    public void updateModified(TravelNote travelNote) {
        travelNote.changeModified(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        travelNoteRepository.merge(travelNote);
    }
}