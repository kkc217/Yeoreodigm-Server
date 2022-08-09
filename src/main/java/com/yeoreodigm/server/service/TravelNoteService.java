package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.*;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.CourseRepository;
import com.yeoreodigm.server.repository.MemberRepository;
import com.yeoreodigm.server.repository.TravelNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public TravelNote findTravelNote(Long travelNoteId) {
        TravelNote travelNote = travelNoteRepository.findById(travelNoteId);
        if (travelNote != null) {
            return travelNote;
        } else {
            throw new BadRequestException("일치하는 여행 메이킹 노트가 없습니다.");
        }
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

        for (int idx = 0; idx < recommendedCourseList.size(); idx++) {
            courseService.saveCourse(travelNote, idx + 1, recommendedCourseList.get(idx));
        }

        return travelNote.getId();

    }

    public TravelNote callNote(Long travelNoteId) {
        return findTravelNote(travelNoteId);
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

}