package org.example.tradingplatform.service;

import org.example.tradingplatform.modal.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findAllByWalletId(Long walletId);
}
