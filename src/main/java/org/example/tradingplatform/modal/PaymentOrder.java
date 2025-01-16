package org.example.tradingplatform.modal;

import jakarta.persistence.*;
import lombok.Data;
import org.example.tradingplatform.domen.PaymentMethod;
import org.example.tradingplatform.domen.PaymentOrderStatus;

@Entity
@Data
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long amount;

    private PaymentOrderStatus status;

    private PaymentMethod paymentMethod;

    @ManyToOne
    private UserEntity user;
}
