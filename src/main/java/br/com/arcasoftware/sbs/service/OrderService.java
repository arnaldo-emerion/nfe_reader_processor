package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.dto.OrderDto;

public interface OrderService {
    void processOrder(OrderDto orderDto);
    OrderDto placeOrder(OrderDto orderDto);
}
