package org.example.tradingplatform.service;

import org.example.tradingplatform.modal.PaymentDetails;
import org.example.tradingplatform.modal.UserEntity;

public interface PaymentDetailsService {

    PaymentDetails addPaymentDetails(String accountNumber, String accountHolderName, String ifsc, String bankName, UserEntity user);

    PaymentDetails getUsersPaymentDetails(UserEntity user);
}
