package org.example.tradingplatform.service;

import org.example.tradingplatform.modal.Wallet;
import org.example.tradingplatform.modal.WalletTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalletTransactionImpl implements WalletTransactionService {

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;
    @Autowired
    private WalletService walletService;

    @Override
    public List<WalletTransaction> getWalletTransactions(Wallet userWallet) {
        return walletTransactionRepository.findAllByWalletId(userWallet.getId());
    }

    @Override
    public WalletTransaction saveWalletTransaction(WalletTransaction walletTransaction) {
        return walletTransactionRepository.save(walletTransaction);
    }
}
