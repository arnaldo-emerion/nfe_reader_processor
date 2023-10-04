package br.com.arcasoftware.sbs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SQSMessageDTO {
    private String fileName;
    private String sequencer;
}
