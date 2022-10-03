package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Frame;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.Photodigm;
import com.yeoreodigm.server.domain.Picture;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.photodigm.*;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.exception.LoginRequiredException;
import com.yeoreodigm.server.service.AwsS3Service;
import com.yeoreodigm.server.service.PhotodigmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.yeoreodigm.server.dto.constraint.AWSConst.AWS_S3_PICTURE_URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/photodigm")
public class PhotodigmApiController {

    private final PhotodigmService photodigmService;

    private final AwsS3Service awsS3Service;

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

        if (Objects.isNull(member) && Objects.isNull(photodigm.getMember())) {
            return new PhotodigmDto(photodigm);
        } else if ((Objects.nonNull(member) && Objects.nonNull(photodigm.getMember()))
                && Objects.equals(member.getId(), photodigm.getMember().getId())) {
            return new PhotodigmDto(photodigm);
        }

        throw new BadRequestException("포토다임 접근 권한이 없습니다.");
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

    @GetMapping("/picture/{photodigmId}")
    public PhotodigmImageDto callPhotodigmImageInfos(
            @PathVariable("photodigmId") Long photodigmId) {
        Photodigm photodigm = photodigmService.getPhotodigm(photodigmId);
        List<Picture> pictureList = photodigmService.getPictureList(photodigm.getPictures());

        return new PhotodigmImageDto(photodigm, pictureList);
    }

    @PutMapping("/picture")
    public PhotodigmImageDto changePhotodigmImages(
            @RequestPart(value = "photodigmId") Long photodigmId,
            @RequestPart(value = "picture1", required = false) MultipartFile picture1,
            @RequestPart(value = "picture2", required = false) MultipartFile picture2,
            @RequestPart(value = "picture3", required = false) MultipartFile picture3,
            @RequestPart(value = "picture4", required = false) MultipartFile picture4,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        Photodigm photodigm = photodigmService.getPhotodigm(photodigmId);

        if (Objects.isNull(member)) {
            if (Objects.nonNull(photodigm.getMember()))
                throw new BadRequestException("포토다임 수정 권한이 없습니다.");
        } else {
            if (Objects.isNull(photodigm.getMember())
                    || !Objects.equals(member.getId(), photodigm.getMember().getId()))
                throw new BadRequestException("포토다임 수정 권한이 없습니다.");
        }

        List<MultipartFile> fileList = new ArrayList<>();
        fileList.add(picture1);
        fileList.add(picture2);
        fileList.add(picture3);
        fileList.add(picture4);

        List<Picture> pictureList = new ArrayList<>();
        for (int i = 0; i < fileList.size(); i++) {
            MultipartFile file = fileList.get(i);
            if (Objects.isNull(file)) {
                pictureList.add(photodigmService.getPicture(photodigm.getPictures().get(i)));
                continue;
            }

            photodigmService.checkPictureContentType(file);
            String pictureAddress = photodigmService.getRandomFileName();
            awsS3Service.uploadFile(AWS_S3_PICTURE_URI, pictureAddress, file);
            pictureList.add(photodigmService.savePicture(pictureAddress, member));
        }

        photodigmService.changePhotodigmPictures(photodigm, pictureList);
        photodigmService.createPhotodigmImage(
                pictureList,
                photodigmService.getFrame(photodigm.getFrameId()),
                photodigm.getAddress());
        photodigmService.savePhotodigm(photodigm);

        return new PhotodigmImageDto(photodigm, pictureList);
    }

    @GetMapping("/frame")
    public Result<List<FrameDto>> callFrames() {
        return new Result<>(photodigmService.getAllFrame()
                .stream()
                .map(FrameDto::new)
                .toList());
    }

    @PutMapping("/frame")
    public PhotodigmImageDto changePhotodigmFrame(
            @RequestBody HashMap<String, Long> request,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        Photodigm photodigm = photodigmService.getPhotodigm(request.get("photodigmId"));

        if (Objects.isNull(member)) {
            if (Objects.nonNull(photodigm.getMember()))
                throw new BadRequestException("포토다임 수정 권한이 없습니다.");
        } else {
            if (Objects.isNull(photodigm.getMember())
                    || !Objects.equals(member.getId(), photodigm.getMember().getId()))
                throw new BadRequestException("포토다임 수정 권한이 없습니다.");
        }

        Frame frame = photodigmService.getFrame(request.get("frameId"));
        photodigmService.changePhotodigmFrame(photodigm, frame);

        List<Picture> pictureList = photodigmService.getPictureList(photodigm.getPictures());
        photodigmService.createPhotodigmImage(
                pictureList,
                frame,
                photodigm.getAddress());
        photodigmService.savePhotodigm(photodigm);

        return new PhotodigmImageDto(photodigm, pictureList);
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

}
