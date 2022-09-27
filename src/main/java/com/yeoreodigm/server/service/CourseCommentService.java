package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Course;
import com.yeoreodigm.server.domain.CourseComment;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.exception.LoginRequiredException;
import com.yeoreodigm.server.repository.CompanionRepository;
import com.yeoreodigm.server.repository.CourseCommentRepository;
import com.yeoreodigm.server.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseCommentService {

    private final CourseCommentRepository courseCommentRepository;

    private final CourseRepository courseRepository;

    private final CompanionRepository companionRepository;

    public List<CourseComment> getCourseCommentsByTravelNoteAndDay(TravelNote travelNote, int day) {
        Course course = courseRepository.findByTravelNoteIdAndDay(travelNote.getId(), day);
        if (course != null) {
            return Objects.requireNonNullElseGet(
                    courseCommentRepository.findCourseCommentsByCourse(course), ArrayList::new);
        } else {
            throw new BadRequestException("일치하는 일차 정보가 없습니다.");
        }
    }

    @Transactional
    public void addCourseComment(TravelNote travelNote, Member member, int day, String text) {
        if (member == null) throw new LoginRequiredException("로그인이 필요합니다.");

        Course course = courseRepository.findByTravelNoteIdAndDay(travelNote.getId(), day);
        if (course == null) {
            throw new BadRequestException("일치하는 일차 정보가 없습니다.");
        } else if (!Objects.equals(member.getId(), course.getTravelNote().getMember().getId())
                && Objects.isNull(companionRepository.findByTravelNoteAndMember(travelNote, member))) {
            throw new BadRequestException("댓글을 작성할 수 있는 권한이 없습니다.");
        }

        courseCommentRepository.saveAndFlush(new CourseComment(course, member, text));
    }

    @Transactional
    public void deleteCourseComment(Member member, Long commentId) {
        if (member == null) throw new LoginRequiredException("로그인이 필요합니다.");

        CourseComment courseComment = courseCommentRepository.findById(commentId);

        if (courseComment != null) {
            if (!Objects.equals(member.getId(), courseComment.getCourse().getTravelNote().getMember().getId())
                    && !Objects.equals(member.getId(), courseComment.getMember().getId())) {
                throw new BadRequestException("댓글을 삭제할 수 있는 권한이 없습니다.");
            }

            courseCommentRepository.deleteByCourseCommentId(courseComment.getId());
        }
    }

}
