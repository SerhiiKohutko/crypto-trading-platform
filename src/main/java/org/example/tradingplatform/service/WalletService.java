package org.example.tradingplatform.service;

import org.example.tradingplatform.modal.Order;
import org.example.tradingplatform.modal.UserEntity;
import org.example.tradingplatform.modal.Wallet;

public interface WalletService {

    Wallet getUserWallet(UserEntity user);
    Wallet addBalance(Wallet wallet, double amount);
    Wallet findWalletById(Long id) throws Exception;
    Wallet walletToWalletTransfer(UserEntity sender, Wallet receiverWallet, double amount) throws Exception;
    Wallet payOrderPayment(Order order, UserEntity user) throws Exception;




}
