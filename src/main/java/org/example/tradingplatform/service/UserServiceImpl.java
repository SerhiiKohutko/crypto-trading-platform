package org.example.tradingplatform.service;

import org.example.tradingplatform.config.JwtProvider;
import org.example.tradingplatform.domen.VerificationType;
import org.example.tradingplatform.modal.TwoFactorAuth;
import org.example.tradingplatform.modal.UserEntity;
import org.example.tradingplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserEntity findUserByJwt(String jwt) throws Exception {
        String email = JwtProvider.getEmailFromToken(jwt);
        UserEntity user = userRepository.findByEmail(email);

        if (user == null) {
            throw new Exception("User not found");
        }

        return user;
    }

    @Override
    public UserEntity findUserByEmail(String email) throws Exception {
        UserEntity user = userRepository.findByEmail(email);

        if (user == null) {
            throw new Exception("User not found");
        }

        return user;
    }

    @Override
    public UserEntity findUserById(long id) throws Exception {
        Optional<UserEntity> user = userRepository.findById(id);
        return user.orElseThrow(Exception::new);
    }

    @Override
    public UserEntity enableTwoFactorAuthentication(VerificationType verificationType,
                                                    String sendTo,
                                                    UserEntity user) {
        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setEnabled(true);
        twoFactorAuth.setSendTo(verificationType);
        user.setTwoFactorAuth(twoFactorAuth);
        return userRepository.save(user);
    }


    @Override
    public UserEntity updatePassword(UserEntity user, String newPassword) {

        user.setPassword(newPassword);

        return userRepository.save(user);
    }
}
