package org.example.tradingplatform.service;

import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;
import org.example.tradingplatform.domen.PaymentMethod;
import org.example.tradingplatform.modal.PaymentOrder;
import org.example.tradingplatform.modal.UserEntity;
import org.example.tradingplatform.response.PaymentResponse;

public interface PaymentService {

    PaymentOrder createPaymentOrder(UserEntity user, Long amount,
                                    PaymentMethod paymentMethod);

    PaymentOrder getPaymentOrder(Long id) throws Exception;

    Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentId) throws RazorpayException, StripeException;

    PaymentResponse createRazorpayPaymentLink(UserEntity user, Long amount) throws RazorpayException;
    PaymentResponse createStripePaymentLink(UserEntity user, Long amount, Long orderId) throws StripeException;
}
