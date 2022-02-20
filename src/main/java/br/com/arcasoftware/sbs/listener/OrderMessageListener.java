package br.com.arcasoftware.sbs.listener;

import br.com.arcasoftware.sbs.service.NfeProcessor;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
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

    public OrderMessageListener(NfeProcessor nfeProcessor) {
        this.nfeProcessor = nfeProcessor;
    }

    @SqsListener(value = QUEUE_NAME, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void processMessage(Object message) {
        try {
            log.info("Received new SQS message: {}", message);

            final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(REGION).build();

            String s3FileName = java.net.URLDecoder.decode(message.toString(), StandardCharsets.UTF_8.name());

            S3Object o = s3.getObject(BUCKET_NAME, s3FileName);

            S3ObjectInputStream s3is = o.getObjectContent();

            String originalFileName = message.toString().split("/")[2];

            this.nfeProcessor.processNFe(s3is, originalFileName, true);

            log.info("Processamento Finalizado");

        } catch (Exception e) {
            log.error("It was not possible to process this NFe because: " + e.getMessage());
        }
    }
}
