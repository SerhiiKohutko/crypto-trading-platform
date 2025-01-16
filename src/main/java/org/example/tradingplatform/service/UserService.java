package org.example.tradingplatform.service;

import org.example.tradingplatform.domen.VerificationType;
import org.example.tradingplatform.modal.UserEntity;

public interface UserService {

    public UserEntity findUserByJwt(String jwt) throws Exception;
    public UserEntity findUserByEmail(String email) throws Exception;
    public UserEntity findUserById(long id) throws Exception;

    UserEntity enableTwoFactorAuthentication(VerificationType verificationType, String sendTo, UserEntity user);

    UserEntity updatePassword(UserEntity user, String newPassword);
}
