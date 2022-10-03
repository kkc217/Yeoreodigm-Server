package com.yeoreodigm.server.dto.photodigm;

import com.yeoreodigm.server.domain.Photodigm;
import com.yeoreodigm.server.domain.Picture;
import com.yeoreodigm.server.dto.constraint.AWSConst;
import lombok.Data;

import java.util.List;

import static com.yeoreodigm.server.dto.constraint.AWSConst.*;

@Data
public class PhotodigmImageUrlDto {

    private String photodigmUrl;

    private String picture1Url;

    private String picture2Url;

    private String picture3Url;

    private String picture4Url;

    public PhotodigmImageUrlDto(Photodigm photodigm, List<Picture> pictureList) {
        this.photodigmUrl = AWS_S3_BASE_URL + AWS_S3_PHOTODIGM_URI + "/" + photodigm.getAddress();
        this.picture1Url = AWS_S3_BASE_URL + AWS_S3_PICTURE_URI + "/" + pictureList.get(0).getAddress();
        this.picture2Url = AWS_S3_BASE_URL + AWS_S3_PICTURE_URI + "/" + pictureList.get(1).getAddress();
        this.picture3Url = AWS_S3_BASE_URL + AWS_S3_PICTURE_URI + "/" + pictureList.get(2).getAddress();
        this.picture4Url = AWS_S3_BASE_URL + AWS_S3_PICTURE_URI + "/" + pictureList.get(3).getAddress();
    }

}
