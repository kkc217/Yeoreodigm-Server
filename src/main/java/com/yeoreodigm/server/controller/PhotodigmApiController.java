package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Frame;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.Photodigm;
import com.yeoreodigm.server.domain.Picture;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.photodigm.PhotodigmDto;
import com.yeoreodigm.server.dto.photodigm.PhotodigmIdDto;
import com.yeoreodigm.server.service.PhotodigmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

}
