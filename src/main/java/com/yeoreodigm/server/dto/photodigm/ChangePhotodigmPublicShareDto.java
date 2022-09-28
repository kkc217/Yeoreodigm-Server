package com.yeoreodigm.server.dto.photodigm;

import lombok.Data;

@Data
public class ChangePhotodigmPublicShareDto {

    private Long photodigmId;

    private boolean publicShare;

}
