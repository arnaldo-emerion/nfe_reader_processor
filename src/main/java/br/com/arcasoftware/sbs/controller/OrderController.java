package br.com.arcasoftware.sbs.controller;

import br.com.arcasoftware.sbs.dto.OrderDto;
import br.com.arcasoftware.sbs.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDto> placeOrder(@RequestBody OrderDto orderDto) {
        return new ResponseEntity<>(this.orderService.placeOrder(orderDto), HttpStatus.OK);
    }

    @GetMapping
    public String ping(){
        return "Esta tudo a correr bem...";
    }
}
