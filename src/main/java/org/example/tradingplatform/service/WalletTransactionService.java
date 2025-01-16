package org.example.tradingplatform.service;

import org.example.tradingplatform.modal.Wallet;
import org.example.tradingplatform.modal.WalletTransaction;

import java.util.List;

public interface WalletTransactionService {
    List<WalletTransaction> getWalletTransactions(Wallet userWallet);
    WalletTransaction saveWalletTransaction(WalletTransaction walletTransaction);
}
