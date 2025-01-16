package org.example.tradingplatform.request;

import lombok.Data;
import org.example.tradingplatform.domen.OrderType;

@Data
public class CreateOrderRequest {
    private String coinId;
    private double quantity;
    private OrderType orderType;
}
