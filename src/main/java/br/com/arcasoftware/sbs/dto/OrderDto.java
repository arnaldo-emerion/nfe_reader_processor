package br.com.arcasoftware.sbs.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class OrderDto {
    private String reference;
    private String userId;
    private String coupon;

}
