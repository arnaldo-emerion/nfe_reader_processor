package br.com.arcasoftware.sbs.listener;

import br.com.arcasoftware.sbs.config.AwsConfigProperties;
import br.com.arcasoftware.sbs.model.dto.SQSMessageDTO;
import br.com.arcasoftware.sbs.model.nfe.ErroProcessamento;
import br.com.arcasoftware.sbs.service.ErroProcessamentoService;
import br.com.arcasoftware.sbs.service.NfeProcessor;
import br.com.arcasoftware.sbs.service.ProcessamentoNFeService;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class NfeXmlListener {

    private final NfeProcessor nfeProcessor;
    private final ProcessamentoNFeService processamentoNFeService;
    private final ErroProcessamentoService erroProcessamentoService;
    private final AwsConfigProperties awsConfig;

    public NfeXmlListener(NfeProcessor nfeProcessor, ProcessamentoNFeService processamentoNFeService, ErroProcessamentoService erroProcessamentoService, AwsConfigProperties awsConfig) {
        this.nfeProcessor = nfeProcessor;
        this.processamentoNFeService = processamentoNFeService;
        this.erroProcessamentoService = erroProcessamentoService;
        this.awsConfig = awsConfig;
    }

    @SqsListener(value = "${aws.config.sqsXml}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void processMessage(Object message) {
        String fileName = null;
        String userName = null;
        try {
            log.info("Received new SQS message: {}", message);

            final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(awsConfig.getRegion()).build();

            SQSMessageDTO sqsMessageDTO = new Gson().fromJson(message.toString(), SQSMessageDTO.class);

            String s3FileName = java.net.URLDecoder.decode(sqsMessageDTO.getFileName(), StandardCharsets.UTF_8.name());

            S3Object o = s3.getObject(awsConfig.getBucketName(), s3FileName);

            String[] pathComposition = s3FileName.split("/");

            fileName = pathComposition[2];

            userName = pathComposition[1].split(":")[1];

            S3ObjectInputStream s3is = o.getObjectContent();

            this.nfeProcessor.processNFe(s3is, fileName, userName);

            log.info("Processamento Finalizado");

        } catch (Exception e) {
            log.error("It was not possible to process this NFe because: " + e.getMessage());
            this.erroProcessamentoService.save(new ErroProcessamento(userName, "Erro ao processar: " + e.getMessage()));
        } finally {
            try {
                this.processamentoNFeService.finalizeProcessamento(userName, fileName);
            } catch (Exception ex) {
                this.erroProcessamentoService.save(new ErroProcessamento(userName, "Erro ao alterar Status: " + ex.getMessage()));
            }
        }
    }
}

