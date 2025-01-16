package org.example.tradingplatform.controller;

import org.example.tradingplatform.domen.PaymentMethod;
import org.example.tradingplatform.modal.PaymentOrder;
import org.example.tradingplatform.modal.UserEntity;
import org.example.tradingplatform.response.PaymentResponse;
import org.example.tradingplatform.service.PaymentService;
import org.example.tradingplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/api/payment/{paymentMethod}/amount/{amount}")
    public ResponseEntity<PaymentResponse> paymentHandler(
            @PathVariable PaymentMethod paymentMethod,
            @PathVariable Long amount,
            @RequestHeader("Authorization") String jwt) throws Exception {

        UserEntity user = userService.findUserByJwt(jwt);

        PaymentResponse paymentResponse;

        PaymentOrder order = paymentService.createPaymentOrder(user, amount, paymentMethod);

        if (paymentMethod.equals(PaymentMethod.RAZORPAY)) {
            paymentResponse = paymentService.createRazorpayPaymentLink(user, amount);
        }else{
            paymentResponse = paymentService.createStripePaymentLink(user, amount, order.getId());
        }
        return ResponseEntity.ok(paymentResponse);
        }

}
