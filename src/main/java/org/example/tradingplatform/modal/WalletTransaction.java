package org.example.tradingplatform.modal;

import jakarta.persistence.*;
import lombok.Data;
import org.example.tradingplatform.domen.WalletTransactionType;

import java.time.LocalDate;

@Data
@Entity
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Wallet wallet;

    private WalletTransactionType type;

    private LocalDate date = LocalDate.now();
    private String transferId;
    private String purpose;
    private Long amount;
}
