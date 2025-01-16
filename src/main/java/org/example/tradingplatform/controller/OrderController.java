package org.example.tradingplatform.controller;

import org.example.tradingplatform.domen.OrderType;
import org.example.tradingplatform.modal.Coin;
import org.example.tradingplatform.modal.Order;
import org.example.tradingplatform.modal.UserEntity;
import org.example.tradingplatform.modal.WalletTransaction;
import org.example.tradingplatform.request.CreateOrderRequest;
import org.example.tradingplatform.service.CoinService;
import org.example.tradingplatform.service.OrderService;
import org.example.tradingplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private CoinService coinService;

    @PostMapping("/pay")
    public ResponseEntity<Order> payOrderPayment(
            @RequestHeader("Authorization") String jwt,
            @RequestBody CreateOrderRequest req)
        throws Exception {

        UserEntity user = userService.findUserByJwt(jwt);
        Coin coin = coinService.findById(req.getCoinId());


        Order order = orderService.processOrder(coin, req.getQuantity(), req.getOrderType(), user);
        WalletTransaction walletTransaction = new WalletTransaction();

        return ResponseEntity.ok(order);
    }

    @PostMapping("/{orderOd}")
    public ResponseEntity<Order> getOrderById(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long orderId) throws Exception{

        UserEntity user = userService.findUserByJwt(jwt);


        Order order = orderService.getOrderById(orderId);

        if (order.getUser().getId().equals(user.getId())) {
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping()
    public ResponseEntity<List<Order>> getAllOrdersForUser(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(required = false) OrderType order_type,
            @RequestParam(required = false) String asset_symbol
    ) throws Exception {
        if (jwt == null) {
            throw new Exception();
        }

        Long userId = userService.findUserByJwt(jwt).getId();

        List<Order> userOrders = orderService.getAllOrdersOfUser(userId, order_type, asset_symbol);
        return ResponseEntity.ok(userOrders);
    }

}
