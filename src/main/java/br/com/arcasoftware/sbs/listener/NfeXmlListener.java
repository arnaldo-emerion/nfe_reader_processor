package br.com.arcasoftware.sbs.listener;

import br.com.arcasoftware.sbs.model.dto.S3ObjectDTO;
import br.com.arcasoftware.sbs.model.dto.SQSMessageDTO;
import br.com.arcasoftware.sbs.model.nfe.ErroProcessamento;
import br.com.arcasoftware.sbs.service.ErroProcessamentoService;
import br.com.arcasoftware.sbs.service.NfeProcessor;
import br.com.arcasoftware.sbs.service.ProcessamentoNFeService;
import br.com.arcasoftware.sbs.service.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NfeXmlListener {

    private final NfeProcessor nfeProcessor;
    private final ProcessamentoNFeService processamentoNFeService;
    private final ErroProcessamentoService erroProcessamentoService;
    private final S3Service s3Service;

    public NfeXmlListener(NfeProcessor nfeProcessor,
                          ProcessamentoNFeService processamentoNFeService,
                          ErroProcessamentoService erroProcessamentoService,
                          S3Service s3Service) {
        this.nfeProcessor = nfeProcessor;
        this.processamentoNFeService = processamentoNFeService;
        this.erroProcessamentoService = erroProcessamentoService;
        this.s3Service = s3Service;
    }

    @SqsListener(value = "${aws.config.sqsXml}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void processMessage(SQSMessageDTO message) {
        S3ObjectDTO s3ObjectDTO = new S3ObjectDTO();
        try {
            log.info("Received new SQS message: {}", message);
            s3ObjectDTO = this.s3Service.getFileFromS3(message.getFileName());
            this.nfeProcessor.processNFe(s3ObjectDTO);
            log.info("Processamento Finalizado");

        } catch (Exception e) {
            log.error("It was not possible to process this NFe because: " + e.getMessage());
            this.erroProcessamentoService.save(new ErroProcessamento(s3ObjectDTO.getUserName(), "Erro ao processar: " + e.getMessage()));
        } finally {
            try {
                this.processamentoNFeService.finalizeProcessamento(s3ObjectDTO.getUserName(), s3ObjectDTO.getFileName());
            } catch (Exception ex) {
                this.erroProcessamentoService.save(new ErroProcessamento(s3ObjectDTO.getUserName(), "Erro ao alterar Status: " + ex.getMessage()));
            }
        }
    }
}

