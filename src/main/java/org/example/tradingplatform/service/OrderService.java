package org.example.tradingplatform.service;

import org.example.tradingplatform.domen.OrderType;
import org.example.tradingplatform.modal.Coin;
import org.example.tradingplatform.modal.Order;
import org.example.tradingplatform.modal.OrderItem;
import org.example.tradingplatform.modal.UserEntity;

import java.util.List;

public interface OrderService {

    Order createOrder(UserEntity user, OrderItem orderItem, OrderType orderType);

    Order getOrderById(Long orderId) throws Exception;
    List<Order> getAllOrdersOfUser(Long userId, OrderType orderType, String assetSymbol);

    Order processOrder(Coin coin, double quantity, OrderType orderType, UserEntity user) throws Exception;




}
