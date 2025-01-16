package org.example.tradingplatform.service;

import org.example.tradingplatform.domen.OrderType;
import org.example.tradingplatform.modal.Order;
import org.example.tradingplatform.modal.UserEntity;
import org.example.tradingplatform.modal.Wallet;
import org.example.tradingplatform.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public Wallet getUserWallet(UserEntity user) {
        Wallet wallet = walletRepository.findByUserId(user.getId());
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUser(user);
            walletRepository.save(wallet);
        }
        return wallet;
    }

    @Override
    public Wallet addBalance(Wallet wallet, double amount) {
        BigDecimal balance = wallet.getBalance();
        balance = balance.add(BigDecimal.valueOf(amount));
        wallet.setBalance(balance);
        return wallet;
    }

    @Override
    public Wallet findWalletById(Long id) throws Exception {
        Optional<Wallet> wallet = walletRepository.findById(id);
        if (wallet.isPresent()) {
            return wallet.get();
        }
        throw new Exception("Wallet not found");
    }

    @Override
    public Wallet walletToWalletTransfer(UserEntity sender, Wallet receiverWallet, double amount) throws Exception {
        Wallet senderWallet = getUserWallet(sender);

        if (senderWallet.getBalance().compareTo(BigDecimal.valueOf(amount))<0){
            throw new Exception("Insufficient balance...");
        }
        BigDecimal senderBalance = senderWallet.getBalance();
        senderBalance = senderBalance.subtract(BigDecimal.valueOf(amount));
        senderWallet.setBalance(senderBalance);
        walletRepository.save(senderWallet);

        receiverWallet
                .setBalance(receiverWallet
                .getBalance()
                .add(BigDecimal.valueOf(amount)));
        walletRepository.save(receiverWallet);

        return senderWallet;
    }

    @Override
    public Wallet payOrderPayment(Order order, UserEntity user) throws Exception {
        Wallet wallet = getUserWallet(user);

        BigDecimal newBalance;
        if(order.getOrderType().equals(OrderType.BUY)) {
            newBalance = wallet.getBalance().subtract(order.getPrice());
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new Exception("Not enough funds for this transaction");
            }

        } else {
            newBalance = wallet.getBalance().add(order.getPrice());
        }
        wallet.setBalance(newBalance);

        return walletRepository.save(wallet);
    }
}
