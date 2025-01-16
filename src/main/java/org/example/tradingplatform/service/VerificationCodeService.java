package org.example.tradingplatform.service;


import org.example.tradingplatform.domen.VerificationType;
import org.example.tradingplatform.modal.UserEntity;
import org.example.tradingplatform.modal.VerificationCode;

public interface VerificationCodeService {
    VerificationCode sendVerificationCode(UserEntity user, VerificationType type);

    VerificationCode getVerificationCode(long id) throws Exception;

    VerificationCode getVerificationCodeByUser(Long userId);


    void deleteVerificationCodeById(VerificationCode verificationCode);
}
