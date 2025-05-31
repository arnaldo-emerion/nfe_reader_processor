package br.com.arcasoftware.sbs.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws.config")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AwsConfigProperties {
    private String region;
    private String bucketName;
}


