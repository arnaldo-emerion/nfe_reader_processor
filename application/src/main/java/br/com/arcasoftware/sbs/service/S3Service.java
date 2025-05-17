package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.config.AwsConfigProperties;
import br.com.arcasoftware.sbs.model.dto.S3ObjectDTO;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class S3Service {

    private final AwsConfigProperties awsConfig;
    private final AmazonS3 amazonS3;

    public S3Service(AwsConfigProperties awsConfig, AmazonS3 amazonS3) {
        this.awsConfig = awsConfig;
        this.amazonS3 = amazonS3;
    }

    public S3ObjectDTO getFileFromS3(String fileNameEncoded) throws UnsupportedEncodingException {
        String s3FileName = java.net.URLDecoder.decode(fileNameEncoded, StandardCharsets.UTF_8.name());
        // private/us-east-1:8b48126a-b84e-486e-8e11-cf6a8bf182b5/920 - NF-e- 35241019355133000109550010000009201700362589.xml
        S3Object o = amazonS3.getObject(awsConfig.getBucketName(), s3FileName);
        String[] pathComposition = s3FileName.split("/");
        String fileName = pathComposition[3];
        String userName = pathComposition[2];
        InputStream inputStream = o.getObjectContent();
        return new S3ObjectDTO(userName, fileName, inputStream);
    }
}
