package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Course;
import com.yeoreodigm.server.domain.CourseComment;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.exception.BadRequestException;
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

    @Transactional
    public CourseComment saveCourseComment(Long travelNoteId, int day, Member member, String text) {

        Course course = courseRepository.findByTravelNoteIdAndDay(travelNoteId, day);
        if (!course.getTravelNote().getCompanion().contains(member.getId())
                && !course.getTravelNote().getMember().getId().equals(member.getId())) {
            throw new BadRequestException("이 여행 메이킹 노트에 댓글을 작성할 수 있는 권한이 없습니다.");
        }

        CourseComment courseComment = new CourseComment(course, member, text);
        courseCommentRepository.saveAndFlush(courseComment);

        return courseComment;

    }

    public List<CourseComment> searchCourseCommentByNoteAndDay(Long travelNoteId, int day) {
        Course course = courseRepository.findByTravelNoteIdAndDay(travelNoteId, day);
        if (course != null) {
            return Objects.requireNonNullElseGet(courseCommentRepository.findByCourse(course), ArrayList::new);
        } else {
            throw new BadRequestException("일치하는 일차 정보가 없습니다.");
        }
    }

    @Transactional
    public void deleteCourseComment(Long commentId, Member member) {

        CourseComment courseComment = courseCommentRepository.findById(commentId);

        if (courseComment != null) {
            if (!courseComment.getCourse().getTravelNote().getMember().getId().equals(member.getId())
                    && !courseComment.getMember().getId().equals(member.getId())) {
                throw new BadRequestException("댓글을 삭제할 수 있는 권한이 없습니다.");
            }

            courseCommentRepository.deleteByCourseComment(courseComment);
        }

    }

}
