package org.example.tradingplatform.service;

import org.example.tradingplatform.modal.Coin;
import org.example.tradingplatform.modal.UserEntity;
import org.example.tradingplatform.modal.Watchlist;

public interface WatchlistService {

    Watchlist findUserWatchlist(Long userId) throws Exception;
    Watchlist createWatchList(UserEntity user);
    Watchlist findById(Long id) throws Exception;

    Coin addItemToWatchlist(Coin coin, UserEntity user) throws Exception;
}
