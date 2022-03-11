package br.com.arcasoftware.sbs.controller;

import br.com.arcasoftware.sbs.enums.MessageType;
import br.com.arcasoftware.sbs.producer.SqsMessageProducer;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("nfe")
@CrossOrigin("*")
public class UploadController {

    private final SqsMessageProducer producer;

    public UploadController(SqsMessageProducer producer) {
        this.producer = producer;
    }

    public ResponseEntity processarNFe(@RequestParam("file") MultipartFile file) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("Message-Type", MessageType.ORDER.name());
        headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        MessageToQueue queue = new MessageToQueue("1234567890", file.getOriginalFilename());

        producer.send(queue, headers);

        return ResponseEntity.ok(queue);
    }
}

@Data
@AllArgsConstructor
class MessageToQueue {
    private String cnpjEmitente;
    private String fileName;
}
