package com.yeoreodigm.server.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.dto.constraint.AWSConst;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static com.yeoreodigm.server.dto.constraint.AWSConst.AWS_S3_BUCKET;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AwsS3Service {

    private final AmazonS3Client amazonS3Client;

    public String uploadFile(String directory, String fileName, MultipartFile multipartFile) {
        validateFileExists(multipartFile);

        String targetUrl = AWS_S3_BUCKET + directory;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(targetUrl, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new BadRequestException("이미지 파일이 존재하지 않습니다.");
        }

        return amazonS3Client.getUrl(targetUrl, fileName).toString();
    }

    private void validateFileExists(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new BadRequestException("이미지 파일이 존재하지 않습니다.");
        }
    }

}
