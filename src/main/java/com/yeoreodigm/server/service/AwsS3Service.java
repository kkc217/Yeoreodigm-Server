package com.yeoreodigm.server.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.yeoreodigm.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.yeoreodigm.server.dto.constraint.AWSConst.AWS_S3_BUCKET;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AwsS3Service {

    private final AmazonS3Client amazonS3Client;

    public String uploadFile(String directory, String fileName, MultipartFile file) {
        validateFileExists(file);

        if (Objects.isNull(fileName)) {
            fileName = getRandomFileName();
        }

        String targetUrl = AWS_S3_BUCKET + directory;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            objectMetadata.setContentLength(bytes.length);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

            amazonS3Client.putObject(new PutObjectRequest(targetUrl, fileName, byteArrayInputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new BadRequestException("이미지 파일이 존재하지 않습니다.");
        }

        return fileName;
    }

    public void uploadFiles(String directory, List<String> fileNameList, List<MultipartFile> fileList) {
        for (int i = 0; i < fileList.size(); i++) {
            uploadFile(directory, fileNameList.get(i), fileList.get(i));
        }
    }

    private void validateFileExists(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new BadRequestException("이미지 파일이 존재하지 않습니다.");
        }
    }

    private String getRandomFileName() {
        return UUID.randomUUID().toString();
    }

}
