package br.com.arcasoftware.sbs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.InputStream;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class S3ObjectDTO {
    private String userName;
    private String fileName;
    private InputStream inputStream;
}
