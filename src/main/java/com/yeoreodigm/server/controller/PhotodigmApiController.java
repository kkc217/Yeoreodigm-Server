package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Frame;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.Photodigm;
import com.yeoreodigm.server.domain.Picture;
import com.yeoreodigm.server.dto.PageResult;
import com.yeoreodigm.server.dto.Result;
import com.yeoreodigm.server.dto.photodigm.*;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.service.AwsS3Service;
import com.yeoreodigm.server.service.MemberService;
import com.yeoreodigm.server.service.PhotodigmService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.yeoreodigm.server.dto.constraint.AWSConst.AWS_S3_PICTURE_URI;
import static com.yeoreodigm.server.dto.constraint.PhotodigmConst.PHOTODIGM_NUMBER_OF_PICTURE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/photodigm")
public class PhotodigmApiController {

    private final PhotodigmService photodigmService;

    private final MemberService memberService;

    private final AwsS3Service awsS3Service;

    @PostMapping("/new")
    public PhotodigmIdDto createPhotodigm(Authentication authentication) {
        Member member = memberService.getMemberByAuthNullable(authentication);

        Photodigm photodigm = photodigmService.createPhotodigm(member);

        List<Picture> pictureList = photodigmService.getPictureList(photodigm.getPictures());
        Frame frame = photodigmService.getFrame(photodigm.getFrameId());
        photodigmService.createPhotodigmImage(pictureList, frame.getAddress(), photodigm.getAddress());

        photodigmService.savePhotodigm(photodigm);

        return new PhotodigmIdDto(photodigm.getId());
    }

    @GetMapping("/{photodigmId}")
    public PhotodigmDto callPhotodigmInfo(
            Authentication authentication,
            @PathVariable("photodigmId") Long photodigmId) {
        Member member = memberService.getMemberByAuthNullable(authentication);

        Photodigm photodigm = photodigmService.getPhotodigm(photodigmId);

        if (Objects.isNull(member)) {
            if (Objects.nonNull(photodigm.getMember()))
                throw new BadRequestException("포토다임 수정 권한이 없습니다.");
        } else {
            if (Objects.isNull(photodigm.getMember())
                    || !Objects.equals(member.getId(), photodigm.getMember().getId()))
                throw new BadRequestException("포토다임 수정 권한이 없습니다.");
        }

        return new PhotodigmDto(photodigm, photodigmService.getFrame(photodigm.getFrameId()));
    }

    @GetMapping("/{page}/{limit}")
    public PageResult<List<PhotodigmDto>> callMyPhotodigmInfos(
            Authentication authentication,
            @PathVariable("page") int page,
            @PathVariable("limit") int limit) {
        Member member = memberService.getMemberByAuthNullable(authentication);

        return new PageResult<>(
                photodigmService.getPhotodigmByMember(member, page, limit)
                        .stream()
                        .map(photodigm -> new PhotodigmDto(photodigm, photodigmService.getFrame(photodigm.getFrameId())))
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
    public void changePhotodigmImages(
            Authentication authentication,
            @RequestPart(name = "photodigmId") Long photodigmId,
            @RequestPart(name = "picture1", required = false) MultipartFile picture1,
            @RequestPart(name = "picture2", required = false) MultipartFile picture2,
            @RequestPart(name = "picture3", required = false) MultipartFile picture3,
            @RequestPart(name = "picture4", required = false) MultipartFile picture4) {
        Member member = memberService.getMemberByAuthNullable(authentication);

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
            if (Objects.isNull(file) || file.isEmpty()) {
                if (Objects.isNull(photodigm.getPictures().get(i)))
                    pictureList.add(null);
                else
                    pictureList.add(photodigmService.getPicture(photodigm.getPictures().get(i)));
                continue;
            }

            String pictureAddress = awsS3Service.uploadFile(AWS_S3_PICTURE_URI, null, file);
            pictureList.add(photodigmService.savePicture(pictureAddress, member));
        }

        photodigmService.changePhotodigmPictures(photodigm, pictureList);
        photodigmService.createPhotodigmImage(
                pictureList,
                photodigm.getAddress(),
                photodigm.getAddress());
        photodigmService.savePhotodigm(photodigm);
    }

    @DeleteMapping("/picture/{photodigmId}/{target}")
    public void deletePhotodigmImage(
            Authentication authentication,
            @PathVariable(name = "photodigmId") Long photodigmId,
            @PathVariable(name = "target") int target) {
        Member member = memberService.getMemberByAuthNullable(authentication);

        Photodigm photodigm = photodigmService.getPhotodigm(photodigmId);

        if (Objects.isNull(member)) {
            if (Objects.nonNull(photodigm.getMember()))
                throw new BadRequestException("포토다임 수정 권한이 없습니다.");
        } else {
            if (Objects.isNull(photodigm.getMember())
                    || !Objects.equals(member.getId(), photodigm.getMember().getId()))
                throw new BadRequestException("포토다임 수정 권한이 없습니다.");
        }

        List<Picture> pictureList = new ArrayList<>();
        for (int i = 0; i < PHOTODIGM_NUMBER_OF_PICTURE; i++) {
            if (target - 1 == i || Objects.isNull(photodigm.getPictures().get(i))) {
                pictureList.add(null);
                continue;
            }
            pictureList.add(photodigmService.getPicture(photodigm.getPictures().get(i)));
        }

        photodigmService.changePhotodigmPictures(photodigm, pictureList);
        photodigmService.createPhotodigmImage(
                pictureList,
                photodigmService.getFrame(photodigm.getFrameId()).getAddress(),
                photodigm.getAddress());
        photodigmService.savePhotodigm(photodigm);
    }

    @GetMapping("/frame")
    public Result<List<FrameDto>> callFrames() {
        return new Result<>(photodigmService.getAllFrame()
                .stream()
                .map(FrameDto::new)
                .toList());
    }

    @PutMapping("/frame")
    public void changePhotodigmFrame(
            Authentication authentication,
            @RequestBody HashMap<String, Long> request) {
        Member member = memberService.getMemberByAuthNullable(authentication);

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
                frame.getAddress(),
                photodigm.getAddress());
        photodigmService.savePhotodigm(photodigm);
    }

    @PatchMapping("/title")
    public void changePhotodigmTitle(
            Authentication authentication,
            @RequestBody @Valid ChangePhotodigmTitleDto requestDto) {
        if (requestDto.getTitle().length() > 30) throw new BadRequestException("포토다임 제목은 30자 이하만 가능합니다.");
        Photodigm photodigm = photodigmService.getPhotodigm(requestDto.getPhotodigmId());

        Member member = memberService.getMemberByAuthNullable(authentication);
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
