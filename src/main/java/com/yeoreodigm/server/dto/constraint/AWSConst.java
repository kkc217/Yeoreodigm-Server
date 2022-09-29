package com.yeoreodigm.server.dto.constraint;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AWSConst {

    public static String AWS_S3_BUCKET;

    public static String AWS_S3_BASE_URL;

    public static String AWS_S3_FRAME_URI;

    public static String AWS_S3_PROFILE_URI;

    public static String AWS_S3_PHOTODIGM_URI;

    public static String AWS_S3_PICTURE_URI;

    @Value("${aws.s3.bucket}")
    public void setAwsS3Bucket(String awsS3Bucket) {
        AWS_S3_BUCKET = awsS3Bucket;
    }

    @Value("${aws.s3.base.url}")
    public void setAwsS3BaseUrl(String awsS3BaseUrl) {
        AWS_S3_BASE_URL = awsS3BaseUrl;
    }

    @Value("${aws.s3.frame.uri}")
    public void setAwsS3FrameUri(String awsS3FrameUri) {
        AWS_S3_FRAME_URI = awsS3FrameUri;
    }

    @Value("${aws.s3.profile.uri}")
    public void setAwsS3ProfileUri(String awsS3ProfileUri) {
        AWS_S3_PROFILE_URI = awsS3ProfileUri;
    }

    @Value("${aws.s3.photodigm.uri}")
    public void setAwsS3PhotodigmUri(String awsS3PhotodigmUri) {
        AWS_S3_PHOTODIGM_URI = awsS3PhotodigmUri;
    }

    @Value("${aws.s3.picture.uri}")
    public void setAwsS3PictureUri(String awsS3PictureUri) {
        AWS_S3_PICTURE_URI = awsS3PictureUri;
    }

}
