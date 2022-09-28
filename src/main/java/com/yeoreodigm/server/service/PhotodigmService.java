package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Frame;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.Photodigm;
import com.yeoreodigm.server.domain.Picture;
import com.yeoreodigm.server.dto.constraint.EnvConst;
import com.yeoreodigm.server.dto.photodigm.CreatePhotodigmImageDto;
import com.yeoreodigm.server.dto.photodigm.PhotodigmImageResponseDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.repository.FrameRepository;
import com.yeoreodigm.server.repository.PhotodigmRepository;
import com.yeoreodigm.server.repository.PictureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.yeoreodigm.server.dto.constraint.PhotodigmConst.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PhotodigmService {

    private final PhotodigmRepository photodigmRepository;

    private final FrameRepository frameRepository;

    private final PictureRepository pictureRepository;

    @Transactional
    public void savePhotodigm(Photodigm photodigm) {
        photodigmRepository.saveAndFlush(photodigm);
    }

    public Photodigm createPhotodigm(Member member) {
        return Photodigm.builder()
                .title("제목을 입력해주세요.")
                .address(createRandomFileName() + PHOTODIGM_EXTENSION)
                .frameId(DEFAULT_FRAME_ID)
                .member(member)
                .pictures(Arrays.asList(PHOTODIGM_DEFAULT_PICTURE_ID_LIST))
                .build();
    }

    public void createPhotodigmImage(List<Picture> pictureList, Frame frame, String fileName) {
        if (pictureList.size() < 4) throw new BadRequestException("첨부된 사진을 확인해주세요.");

        CreatePhotodigmImageDto PhotodigmImageRequestDto = new CreatePhotodigmImageDto(
                pictureList.get(0).getAddress(),
                pictureList.get(1).getAddress(),
                pictureList.get(2).getAddress(),
                pictureList.get(3).getAddress(),
                frame.getAddress(),
                fileName);

        WebClient webClient = WebClient.create(EnvConst.PHOTODIGM_URL);

        Mono<PhotodigmImageResponseDto> apiResult = webClient
                .post()
                .uri(EnvConst.PHOTODIGM_URI)
                .bodyValue(PhotodigmImageRequestDto)
                .retrieve()
                .onStatus(
                        HttpStatus::is4xxClientError,
                        clientResponse -> {
                            throw new BadRequestException("포토다임 생성을 실패하였습니다.");
                        })
                .bodyToMono(PhotodigmImageResponseDto.class);

        try {
            if (!Objects.equals(Objects.requireNonNull(apiResult.block()).getStatusCode(), 200))
                throw new BadRequestException("포토다임 생성에 실패하였습니다.(code: 400)");
        } catch (WebClientResponseException | NullPointerException e) {
            throw new BadRequestException("포토다임 생성에 실패하였습니다.(code: 409");
        }
    }

    private String createRandomFileName() {
        return UUID.randomUUID().toString();
    }

    public Photodigm getPhotodigm(Long photodigmId) {
        Photodigm photodigm = photodigmRepository.findById(photodigmId);

        if (photodigm == null) throw new BadRequestException("포토다임을 찾을 수 없습니다.");
        return photodigm;
    }

    public Frame getFrame(Long frameId) {
        Frame frame = frameRepository.findById(frameId);

        if (frame == null) throw new BadRequestException("프레임을 찾을 수 없습니다.");
        return frame;
    }

    public List<Frame> getAllFrame() {
        return frameRepository.findAll();
    }

    public List<Picture> getPictureList(List<Long> pictureIdList) {
        return pictureIdList.stream().map(pictureRepository::findById).toList();
    }

    public List<Photodigm> getPhotodigmByMember(Member member, int page, int limit) {
        return photodigmRepository.findByMemberPaging(member, limit * (page - 1), limit);
    }

    public int checkNextPhotodigmByMember(Member member, int page, int limit) {
        return getPhotodigmByMember(member, page + 1, limit).size() > 0 ? page + 1 : 0;
    }

    @Transactional
    public void changePhotodigmTitle(Photodigm photodigm, String title) {
        photodigm.changeTitle(title);
        photodigmRepository.merge(photodigm);
    }

}
