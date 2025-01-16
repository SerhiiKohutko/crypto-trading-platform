package org.example.tradingplatform.service;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.example.tradingplatform.domen.PaymentMethod;
import org.example.tradingplatform.domen.PaymentOrderStatus;
import org.example.tradingplatform.modal.PaymentOrder;
import org.example.tradingplatform.modal.UserEntity;
import org.example.tradingplatform.repository.PaymentRepository;
import org.example.tradingplatform.response.PaymentResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Value("sk_test_51QdASjDjB1NHUeBfJwHzy0i7SjUoLIdWkqL6Rs4KCHj5mmii7EuetiwXLS6kPvy46JgCxE9MKjebHxqihrJrLkbV00CoFQNBp6")
    private String stripeSecretKey;

    @Value("${razorpay.api.key}")
    private String apiKey;
    
    @Value("${razorpay.api.secret}")
    private String apiSecretKey;

    @Override
    public PaymentOrder createPaymentOrder(UserEntity user, Long amount, PaymentMethod paymentMethod) {
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setAmount(amount);
        paymentOrder.setPaymentMethod(paymentMethod);
        paymentOrder.setUser(user);
        paymentOrder.setStatus(PaymentOrderStatus.PENDING);
        return paymentRepository.save(paymentOrder);
    }

    @Override
    public PaymentOrder getPaymentOrder(Long id) throws Exception {
        return paymentRepository.findById(id).orElseThrow(() -> new Exception("Payment order not found"));
    }

    public Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentId) throws StripeException {
        if (paymentOrder.getStatus().equals(PaymentOrderStatus.PENDING)) {
            if (paymentOrder.getPaymentMethod().equals(PaymentMethod.STRIPE)) {
                // Устанавливаем секретный ключ Stripe
                Stripe.apiKey = stripeSecretKey;

                String paymentIntentId = paymentId; // Предположим, что paymentId — это ID PaymentIntent

                // Попытка извлечь PaymentIntent
                PaymentIntent paymentIntent;
                try {
                    paymentIntent = PaymentIntent.retrieve(paymentIntentId);
                } catch (StripeException e) {
                    // Если ошибка, попробуем считать, что это ID Session
                    try {
                        Session session = Session.retrieve(paymentId);
                        paymentIntentId = session.getPaymentIntent();
                        paymentIntent = PaymentIntent.retrieve(paymentIntentId);
                    } catch (StripeException ex) {
                        paymentOrder.setStatus(PaymentOrderStatus.FAILED);
                        paymentRepository.save(paymentOrder);
                        System.err.println("Error retrieving PaymentIntent or Session: " + ex.getMessage());
                        return false;
                    }
                }

                // Получаем статус платежа
                String status = paymentIntent.getStatus();
                System.out.println("PaymentIntent Status: " + status);

                if ("succeeded".equals(status)) {
                    paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
                    paymentRepository.save(paymentOrder);
                    return true;
                } else {
                    paymentOrder.setStatus(PaymentOrderStatus.FAILED);
                    paymentRepository.save(paymentOrder);
                    return false;
                }
            }

            // Для других методов оплаты
            paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
            paymentRepository.save(paymentOrder);
            return true;
        }

        return false;
    }

    @Override
    public PaymentResponse createRazorpayPaymentLink(UserEntity user, Long amount) throws RazorpayException {

        Long longAmount = amount * 100;

        try {
            RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecretKey);

            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount", longAmount);
            paymentLinkRequest.put("currency", "INR");

            JSONObject customer = new JSONObject();
            customer.put("name", user.getFullName());
            customer.put("email", user.getEmail());
            paymentLinkRequest.put("customer", customer);

            JSONObject notify = new JSONObject();
            notify.put("email", true);
            paymentLinkRequest.put("notify", notify);

            paymentLinkRequest.put("reminder_enable", true);

            paymentLinkRequest.put("callback_url", "http://localhost:5173/wallet");
            paymentLinkRequest.put("callback_method", "get");

            PaymentLink paymentLink = razorpayClient.paymentLink.create(paymentLinkRequest);

            String paymentLinkId = paymentLink.get("id");
            String paymentLinkUrl = paymentLink.get("short_url");

            PaymentResponse res = new PaymentResponse();
            res.setPayment_url(paymentLinkUrl);

            return res;

        } catch (RazorpayException e) {
            System.out.println(e.getMessage());
            throw new RazorpayException(e.getMessage());
        }

    }

    @Override
    public PaymentResponse createStripePaymentLink(UserEntity user, Long amount, Long orderId) throws StripeException {

        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:5173/wallet?order_id="+orderId+"&payment_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:5173/payment/cancel")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(amount*100)
                                .setProductData(SessionCreateParams
                                        .LineItem
                                        .PriceData
                                        .ProductData
                                        .builder()
                                        .setName("Top up wallet")
                                        .build())
                                .build())
                        .build())
                .build();

         Session session = Session.create(params);

        System.out.println("session _____" + session);
        PaymentResponse res = new PaymentResponse();
        res.setPayment_url(session.getUrl());
        return res;
    }
}
