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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import springfox.documentation.spring.web.json.Json;

import java.util.*;

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
                .address(getRandomFileName() + PHOTODIGM_EXTENSION)
                .frameId(DEFAULT_FRAME_ID)
                .member(member)
                .build();
    }

    public void changePhotodigmPictures(Photodigm photodigm, List<Picture> pictureList) {
        List<Long> pictureIdList = new ArrayList<>();
        for (Picture picture : pictureList) {
            if (Objects.isNull(picture)) {
                pictureIdList.add(null);
                continue;
            }
            pictureIdList.add(picture.getId());
        }
        photodigm.changePictures(pictureIdList);
    }

    public void changePhotodigmFrame(Photodigm photodigm, Frame frame) {
        photodigm.changeFrame(frame.getId());
    }

    public void createPhotodigmImage(List<Picture> pictureList, String baseImageAddress, String fileName) {
        if (pictureList.size() < 4) throw new BadRequestException("첨부된 사진을 확인해주세요.");

        CreatePhotodigmImageDto PhotodigmImageRequestDto = new CreatePhotodigmImageDto(
                Objects.isNull(pictureList.get(0)) ? null : pictureList.get(0).getAddress(),
                Objects.isNull(pictureList.get(1)) ? null : pictureList.get(1).getAddress(),
                Objects.isNull(pictureList.get(2)) ? null : pictureList.get(2).getAddress(),
                Objects.isNull(pictureList.get(3)) ? null : pictureList.get(3).getAddress(),
                baseImageAddress,
                fileName);


        WebClient webClient = WebClient.create(EnvConst.PHOTODIGM_URL);

        webClient
                .post()
                .uri(EnvConst.PHOTODIGM_URI)
                .bodyValue(PhotodigmImageRequestDto)
                .retrieve()
                .onStatus(
                        HttpStatus::is4xxClientError,
                        clientResponse -> Mono.error(new BadRequestException("포토다임 생성을 실패하였습니다.(code: 4xx)")))
                .onStatus(
                        HttpStatus::is5xxServerError,
                        clientResponse -> Mono.error(new BadRequestException("포토다임 생성을 실패하였습니다.(code: 5xx)")))
                .bodyToMono(Void.class)
                .block();
    }

    public String getRandomFileName() {
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

    public Picture getPicture(Long pictureId) {
        Picture picture = pictureRepository.findById(pictureId);

        if (picture == null) throw new BadRequestException("사진을 불러올 수 없습니다.");

        return picture;
    }

    public List<Picture> getPictureList(List<Long> pictureIdList) {
        List<Picture> result = new ArrayList<>();

        for (Long pictureId : pictureIdList) {
            if (Objects.isNull(pictureId)) {
                result.add(null);
                continue;
            }
            result.add(pictureRepository.findById(pictureId));
        }
        return result;
    }

    public List<Photodigm> getPhotodigmByMember(Member member, int page, int limit) {
        return photodigmRepository.findByMemberPaging(member, limit * (page - 1), limit);
    }

    public int checkNextPhotodigmByMember(Member member, int page, int limit) {
        return getPhotodigmByMember(member, page + 1, limit).size() > 0 ? page + 1 : 0;
    }

    @Transactional
    public void changePhotodigmTitle(Photodigm photodigm, String title) {
        if (title.length() > 30) throw new BadRequestException("포토다임 제목은 30자 이하만 가능합니다.");
        photodigm.changeTitle(title);
        photodigmRepository.merge(photodigm);
    }

    public String checkPictureContentType(MultipartFile file) {
        if (file.isEmpty()) throw new BadRequestException("업로드한 이미지 파일을 확인해주시기 바랍니다.");

        if (!Arrays.asList(PICTURE_CONTENT_TYPE_LIST)
                .contains(Objects.requireNonNull(file.getContentType()).toLowerCase()))
            throw new BadRequestException("jpg, jpeg, png, peng 파일만 업로드 가능합니다.");

        return file.getContentType().split("/")[1];
    }

    public void checkPictureListContentType(List<MultipartFile> fileList) {
        for (MultipartFile file : fileList)
            checkPictureContentType(file);
    }

    @Transactional
    public Picture savePicture(String address, Member member) {
        Picture picture = new Picture(address, member);
        pictureRepository.save(picture);
        return picture;
    }

}
