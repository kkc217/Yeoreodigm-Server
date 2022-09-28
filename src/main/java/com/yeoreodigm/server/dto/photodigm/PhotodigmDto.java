package com.yeoreodigm.server.dto.photodigm;

import com.yeoreodigm.server.domain.Photodigm;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.yeoreodigm.server.dto.constraint.AWSConst.AWS_S3_BASE_URL;
import static com.yeoreodigm.server.dto.constraint.AWSConst.AWS_S3_PHOTODIGM_URI;

@Data
public class PhotodigmDto {

    private Long photodigmId;

    private String title;

    private Long frameId;

    private String imageUrl;

    private boolean hasModified;

    private LocalDateTime dateTime;

    public PhotodigmDto(Photodigm photodigm) {
        this.photodigmId = photodigm.getId();
        this.title = photodigm.getTitle();
        this.frameId = photodigm.getFrameId();
        this.imageUrl = AWS_S3_BASE_URL + AWS_S3_PHOTODIGM_URI + "/" + photodigm.getAddress();
        this.hasModified = !Objects.equals(photodigm.getCreated(), photodigm.getModified());
        this.dateTime = photodigm.getModified();
    }

}
