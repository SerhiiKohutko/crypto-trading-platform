package org.example.tradingplatform.service;

import org.example.tradingplatform.modal.TwoFactorOTP;
import org.example.tradingplatform.modal.UserEntity;

public interface TwoFactorOTPService {

    TwoFactorOTP createTwoFactorOTP(UserEntity user, String otp, String jwt);

    TwoFactorOTP findByUser(long userId);

    TwoFactorOTP findById(String id);

    boolean verifyTwoFactor(TwoFactorOTP twoFactorOTP, String otp);

    void deleteTwoFactorOTP(TwoFactorOTP twoFactorOTP);
}
