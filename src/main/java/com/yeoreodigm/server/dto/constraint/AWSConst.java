package com.yeoreodigm.server.dto.constraint;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AWSConst {

    public static String AWS_S3_BUCKET;

    public static String AWS_S3_BASE_URL = "https://yeoreodigm-s3.s3.ap-northeast-2.amazonaws.com";

    public static String AWS_S3_FRAME_URI = "/assets/photoDigm/frame";

    public static String AWS_S3_PROFILE_URI = "/assets/photoDigm/profile";

    public static String AWS_S3_PHOTODIGM_URI = "/assets/photoDigm/photoDigm";

    public static String AWS_S3_PICTURE_URI = "/assets/photoDigm/userPicture";

    @Value("${aws.s3.bucket}")
    public void setAwsS3Bucket(String awsS3Bucket) {
        AWS_S3_BUCKET = awsS3Bucket;
    }

}
