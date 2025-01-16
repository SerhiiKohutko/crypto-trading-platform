package org.example.tradingplatform.service;

import org.example.tradingplatform.modal.UserEntity;
import org.example.tradingplatform.modal.Withdrawal;

import java.util.List;

public interface WithdrawalService {

    Withdrawal requestWithdrawal(Long amount, UserEntity user);

    Withdrawal proceedWithwithdrawal(Long withdrawalId, boolean accept) throws Exception;

    List<Withdrawal> getUsersWithdrawalHistory(UserEntity user);

    List<Withdrawal> getAllWithdrawalRequest();
}
