package org.example.tradingplatform.service;

import org.example.tradingplatform.domen.OrderStatus;
import org.example.tradingplatform.domen.OrderType;
import org.example.tradingplatform.modal.*;
import org.example.tradingplatform.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private WalletService walletService;
    @Autowired
    private AssetService assetService;

    @Override
    public Order createOrder(UserEntity user, OrderItem orderItem, OrderType orderType) {
        double price = orderItem.getCoin().getCurrentPrice()*orderItem.getQuantity();

        Order order = new Order();
        order.setUser(user);
        order.setOrderType(orderType);
        order.setOrderItem(orderItem);
        order.setPrice(BigDecimal.valueOf(price));
        order.setTimeStamp(LocalDate.now());
        order.setStatus(OrderStatus.PENDING);

        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long orderId) throws Exception {
        return orderRepository.findById(orderId).orElseThrow(() -> new Exception("Order not found"));
    }

    @Override
    public List<Order> getAllOrdersOfUser(Long userId, OrderType orderType, String assetSymbol) {
        return orderRepository.findByUserId(userId);
    }

    private OrderItem createOrderItem(Coin coin, double quantity, double buyPrice, double sellPrice){
        OrderItem orderItem = new OrderItem();
        orderItem.setCoin(coin);
        orderItem.setQuantity(quantity);
        orderItem.setBuyPrice(buyPrice);
        orderItem.setSellPrice(sellPrice);
        return orderItem;
    }

    @Transactional
    public Order buyAsset(Coin coin, double quantity, UserEntity user) throws Exception {
        if (quantity<=0){
            throw new Exception("quantity should be > 0");
        }

        double buyPrice = coin.getCurrentPrice();

        OrderItem orderItem = createOrderItem(coin, quantity, buyPrice, 0);

        Order order = createOrder(user, orderItem, OrderType.BUY);
        orderItem.setOrder(order);

        walletService.payOrderPayment(order, user);

        order.setStatus(OrderStatus.SUCCESS);
        order.setOrderType(OrderType.BUY);

        Order savedOrder= orderRepository.save(order);

        Asset oldAsset = assetService.findAssetByUserIdAndCoinId(order.getUser().getId(), order.getOrderItem().getCoin().getId());
        if (oldAsset == null) {
            assetService.createAsset(user, orderItem.getCoin(), orderItem.getQuantity());
        }else {
            assetService.updateAsset(oldAsset.getId(), quantity);
        }

        return savedOrder;
    }

    @Transactional
    public Order sellAsset(Coin coin, double quantity, UserEntity user) throws Exception {
        if (quantity<=0){
            throw new Exception("quantity should be > 0");
        }

        double sellPrice = coin.getCurrentPrice();

        Asset assetToSell = assetService.findAssetByUserIdAndCoinId(user.getId(), coin.getId());

        if (assetToSell != null) {
            OrderItem orderItem = createOrderItem(coin, quantity, 0, sellPrice);


            double buyPrice = assetToSell.getBuyPrice();

            Order order = createOrder(user, orderItem, OrderType.SELL);
            orderItem.setOrder(order);

            if (assetToSell.getQuantity() >= quantity) {
                order.setStatus(OrderStatus.SUCCESS);
                order.setOrderType(OrderType.SELL);

                Order savedOrder = orderRepository.save(order);
                walletService.payOrderPayment(order, user);

                Asset updatedAsset = assetService.updateAsset(assetToSell.getId(), -quantity);
                if (updatedAsset.getQuantity() * coin.getCurrentPrice() <= 1) {
                    assetService.deleteAsset(updatedAsset.getId());
                }
                return savedOrder;
            }

            throw new Exception("quantity should be > 0");
        }

        throw new Exception("Asset not found");

    }

    @Override
    @Transactional
    public Order processOrder(Coin coin, double quantity, OrderType orderType, UserEntity user) throws Exception {
        if (orderType == OrderType.BUY) {
            return buyAsset(coin, quantity, user);
        } else if (orderType == OrderType.SELL) {
            return sellAsset(coin, quantity, user);
        }
        throw new Exception("invalid order type");
    }
}
