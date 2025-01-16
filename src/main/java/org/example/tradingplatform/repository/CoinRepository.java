package org.example.tradingplatform.repository;

import org.example.tradingplatform.modal.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinRepository extends JpaRepository<Coin, String> {
}
