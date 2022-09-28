package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Frame;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.Photodigm;
import com.yeoreodigm.server.domain.Picture;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.photodigm.*;
import com.yeoreodigm.server.dto.travelnote.NoteTitleRequestDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.exception.LoginRequiredException;
import com.yeoreodigm.server.service.PhotodigmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/photodigm")
public class PhotodigmApiController {

    private final PhotodigmService photodigmService;

    @PostMapping("/new")
    public PhotodigmIdDto createPhotodigm(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        Photodigm photodigm = photodigmService.createPhotodigm(member);

        List<Picture> pictureList = photodigmService.getPictureList(photodigm.getPictures());
        Frame frame = photodigmService.getFrame(photodigm.getFrameId());
        photodigmService.createPhotodigmImage(pictureList, frame, photodigm.getAddress());

        photodigmService.savePhotodigm(photodigm);

        return new PhotodigmIdDto(photodigm.getId());
    }

    @GetMapping("/{photodigmId}")
    public PhotodigmDto callPhotodigmInfo(
            @PathVariable("photodigmId") Long photodigmId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        Photodigm photodigm = photodigmService.getPhotodigm(photodigmId);

        return new PhotodigmDto(photodigm);
    }

    @GetMapping("/{page}/{limit}")
    public PageResult<List<PhotodigmDto>> callMyPhotodigmInfos(
            @PathVariable("page") int page,
            @PathVariable("limit") int limit,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        if (member == null) throw new LoginRequiredException("로그인이 필요합니다.");

        return new PageResult<>(
                photodigmService.getPhotodigmByMember(member, page, limit)
                        .stream()
                        .map(PhotodigmDto::new)
                        .toList(),
                photodigmService.checkNextPhotodigmByMember(member, page, limit));
    }

    @GetMapping("/frame")
    public Result<List<FrameDto>> callFrames() {
        return new Result<>(photodigmService.getAllFrame()
                .stream()
                .map(FrameDto::new)
                .toList());
    }

    @PatchMapping("/title")
    public void changePhotodigmTitle(
            @RequestBody @Valid ChangePhotodigmTitleDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        Photodigm photodigm = photodigmService.getPhotodigm(requestDto.getPhotodigmId());

        if (Objects.isNull(member) && Objects.isNull(photodigm.getMember())) {
            photodigmService.changePhotodigmTitle(photodigm, requestDto.getTitle());
            return;
        } else if ((Objects.nonNull(member) && Objects.nonNull(photodigm.getMember()))
                && Objects.equals(member.getId(), photodigm.getMember().getId())) {
            photodigmService.changePhotodigmTitle(photodigm, requestDto.getTitle());
            return;
        }

        throw new BadRequestException("포토다임 제목을 수정할 수 없습니다.");
    }

    @PatchMapping("/public-share")
    public void changePhotodigmPublicShare(
            @RequestBody @Valid ChangePhotodigmPublicShareDto requestDto,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        Photodigm photodigm = photodigmService.getPhotodigm(requestDto.getPhotodigmId());

        if (Objects.isNull(member)
                || Objects.isNull(photodigm.getMember())
                || !Objects.equals(photodigm.getMember().getId(), member.getId()))
            throw new BadRequestException("포토다임 공유 여부를 수정할 수 없습니다.");

        photodigmService.changePhotodigmPublicShare(photodigm, requestDto.isPublicShare());
    }

}
