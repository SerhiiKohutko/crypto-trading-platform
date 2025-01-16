package org.example.tradingplatform.service;

import org.example.tradingplatform.domen.VerificationType;
import org.example.tradingplatform.modal.ForgotPasswordToken;
import org.example.tradingplatform.modal.UserEntity;

public interface ForgotPasswordService {

    ForgotPasswordToken createToken(UserEntity user,
                                    String id, String otp,
                                    VerificationType type, String sendTo);

    ForgotPasswordToken findById(String id);

    ForgotPasswordToken findByUser(long userId);

    void deleteToken(ForgotPasswordToken token);
}
