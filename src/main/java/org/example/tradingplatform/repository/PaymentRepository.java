package org.example.tradingplatform.repository;

import org.example.tradingplatform.modal.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentOrder, Long> {

}
