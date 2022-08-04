package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Course;
import com.yeoreodigm.server.domain.CourseComment;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.CourseCommentRepository;
import com.yeoreodigm.server.repository.CourseRepository;
import com.yeoreodigm.server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseCommentService {

    private final CourseCommentRepository courseCommentRepository;

    private final CourseRepository courseRepository;

    private final MemberRepository memberRepository;

    @Transactional
    public CourseComment saveCourseComment(Long travelNoteId, int day, Member member, String text) {

        Course course = courseRepository.findByTravelNoteIdAndDay(travelNoteId, day);
        if (!course.getTravelNote().getCompanion().contains(member.getId())
                && !course.getTravelNote().getMember().equals(member)) {
            throw new BadRequestException("이 여행 메이킹 노트에 댓글을 작성할 수 있는 권한이 없습니다.");
        }

        CourseComment courseComment = new CourseComment(course, member, text);
        courseCommentRepository.saveAndFlush(courseComment);

        return courseComment;

    }

}
