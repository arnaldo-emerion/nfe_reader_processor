package br.com.arcasoftware.sbs.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jvnet.hk2.annotations.Service;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

import java.util.Collections;

@Service
public class SQSListenerBean {
    @Bean
    public QueueMessageHandlerFactory queueMessageHandlerFactory(ObjectMapper objectMapper) {
        QueueMessageHandlerFactory factory = new QueueMessageHandlerFactory();

        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setSerializedPayloadClass(String.class);
        messageConverter.setObjectMapper(objectMapper);
        messageConverter.setStrictContentTypeMatch(false); // This helps in absence of content-type headers

        factory.setMessageConverters(Collections.singletonList(messageConverter));
        return factory;
    }

}
