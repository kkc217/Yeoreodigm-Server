package com.yeoreodigm.server.dto.photodigm;

import com.yeoreodigm.server.domain.Frame;
import com.yeoreodigm.server.dto.constraint.AWSConst;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class FrameDto {

    private Long frameId;

    private String title;

    private String imageUrl;

    public FrameDto(Frame frame) {
        this.frameId = frame.getId();
        this.title = frame.getTitle();
        this.imageUrl = AWSConst.AWS_S3_BASE_URL + AWSConst.AWS_S3_FRAME_URI + "/" + frame.getAddress();
    }

}
