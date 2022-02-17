package br.com.arcasoftware.sbs.service.impl;

import br.com.arcasoftware.sbs.dto.OrderDto;
import br.com.arcasoftware.sbs.enums.MessageType;
import br.com.arcasoftware.sbs.producer.SqsMessageProducer;
import br.com.arcasoftware.sbs.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final SqsMessageProducer producer;

    public OrderServiceImpl(SqsMessageProducer producer) {
        this.producer = producer;
    }

    @Override
    public void processOrder(OrderDto orderDto) {
        log.debug(" {} Process order from queue {} " +  orderDto);
        /*
        do the good order processing logic here
        not going to do that here, thats not the point of this demo app

         */
    }

    @Override
    public OrderDto placeOrder(OrderDto orderDto) {
        log.debug(" {} Place order {} "  + orderDto);

        Map<String, Object> headers = new HashMap<>();
        headers.put("Message-Type", MessageType.ORDER.name());
        headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        log.debug("{} Send message to Queue {} ");
        this.producer.send(orderDto, headers);
        return orderDto;
    }
}
