package br.com.arcasoftware.sbs.listener;

import br.com.arcasoftware.sbs.service.NfeProcessor;
import br.com.arcasoftware.sbs.service.StatusProcessamentoNFeService;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class OrderMessageListener {

    private static final String BUCKET_NAME = "nfereader232856-dev";
    private static final String REGION = "us-east-1";
    private static final String QUEUE_NAME = "https://sqs.us-east-1.amazonaws.com/492510987777/nfeReaderQueue";
    private final NfeProcessor nfeProcessor;
    private final StatusProcessamentoNFeService statusProcessamentoNFeService;

    public OrderMessageListener(NfeProcessor nfeProcessor, StatusProcessamentoNFeService statusProcessamentoNFeService) {
        this.nfeProcessor = nfeProcessor;
        this.statusProcessamentoNFeService = statusProcessamentoNFeService;
    }

    @SqsListener(value = QUEUE_NAME, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void processMessage(Object message) {
        String sequencer = null;
        try {
            log.info("Received new SQS message: {}", message);

            final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(REGION).build();

            SQSMessage sqsMessage = new Gson().fromJson(message.toString(), SQSMessage.class);

            String s3FileName = java.net.URLDecoder.decode(sqsMessage.getFileName(), StandardCharsets.UTF_8.name());

            S3Object o = s3.getObject(BUCKET_NAME, s3FileName);

            sequencer = sqsMessage.getSequencer();

            this.statusProcessamentoNFeService.markAsProcessing(sequencer);

            S3ObjectInputStream s3is = o.getObjectContent();

            String[] pathComposition = s3FileName.split("/");

            String originalFileName = pathComposition[2];

            String userName = pathComposition[1].split(":")[1];

            this.nfeProcessor.processNFe(s3is, originalFileName, userName);

            this.statusProcessamentoNFeService.updateSucessoProcessamento(sequencer);

            log.info("Processamento Finalizado");

        } catch (Exception e) {
            if (null != sequencer) {
                this.statusProcessamentoNFeService.updateErroProcessamento(sequencer, e.getMessage());
            }
            log.error("It was not possible to process this NFe because: " + e.getMessage());
        }
    }
}

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
class SQSMessage{
    private String fileName;
    private String sequencer;
}