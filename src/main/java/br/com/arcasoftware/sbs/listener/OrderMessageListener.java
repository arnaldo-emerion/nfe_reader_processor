package br.com.arcasoftware.sbs.listener;

import br.com.arcasoftware.sbs.service.NfeProcessor;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import br.com.arcasoftware.sbs.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderMessageListener {

    public static final String BUCKET_NAME = "nfe-reader-xml";
    private static final ObjectMapper OBJECT_MAPPER = Jackson2ObjectMapperBuilder.json().build();
    private final OrderService orderService;
    private final NfeProcessor nfeProcessor;

    public OrderMessageListener(OrderService orderService, NfeProcessor nfeProcessor) {
        this.orderService = orderService;
        this.nfeProcessor = nfeProcessor;
    }

    @SqsListener(value = "https://sqs.us-east-1.amazonaws.com/492510987777/FileUploadQueue", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void processMessage(Object message) {
        try {
            log.info("Received new SQS message: {}", message);

            final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("us-east-1").build();


            S3Object o = s3.getObject(BUCKET_NAME, message.toString());
            S3ObjectInputStream s3is = o.getObjectContent();

            this.nfeProcessor.processNFe(s3is, message.toString().split("/")[0], true);

            log.info("Processamento Finalizado");

        } catch (Exception e) {
            throw new RuntimeException("Cannot process message from SQS", e);
        }
    }
}
