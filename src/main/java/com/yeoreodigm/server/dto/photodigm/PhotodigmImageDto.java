package com.yeoreodigm.server.dto.photodigm;

import com.yeoreodigm.server.domain.Photodigm;
import com.yeoreodigm.server.domain.Picture;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.yeoreodigm.server.dto.constraint.AWSConst.*;

@Data
public class PhotodigmImageDto {

    private Long photodigmId;

    private String photodigmUrl;

    private List<Long> pictureIds = new ArrayList<>();

    private List<String> pictureUrls = new ArrayList<>();

    public PhotodigmImageDto(Photodigm photodigm, List<Picture> pictureList) {
        this.photodigmId = photodigm.getId();
        this.photodigmUrl = AWS_S3_BASE_URL + AWS_S3_PHOTODIGM_URI + "/" + photodigm.getAddress();
        for (Picture picture : pictureList) {
            if (Objects.isNull(picture)) {
                this.pictureIds.add(null);
                this.pictureUrls.add(null);
                continue;
            }
            this.pictureIds.add(picture.getId());
            this.pictureUrls.add(AWS_S3_BASE_URL + AWS_S3_PICTURE_URI + "/" + picture.getAddress());
        }
    }

}
