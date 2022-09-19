package com.yeoreodigm.server.dto.constraint;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AWSConst {

    public static String AWS_S3_BUCKET;

    public static String AWS_S3_PROFILE_URI = "/assets/profile";

    @Value("${aws.s3.bucket}")
    public void setAwsS3Bucket(String awsS3Bucket) {
        AWS_S3_BUCKET = awsS3Bucket;
    }

}
