package org.example.tradingplatform.service;

import org.example.tradingplatform.modal.Coin;
import org.example.tradingplatform.modal.UserEntity;
import org.example.tradingplatform.modal.Watchlist;
import org.example.tradingplatform.repository.WatchlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WatchlistServiceImpl implements WatchlistService{
    @Autowired
    private WatchlistRepository watchlistRepository;

    @Override
    public Watchlist findUserWatchlist(Long userId) throws Exception {
        Watchlist watchlist = watchlistRepository.findByUserId(userId);
        if (watchlist == null) {
            throw new Exception("Watchlist not found");
        }
        return watchlistRepository.save(watchlist);
    }

    @Override
    public Watchlist createWatchList(UserEntity user) {
        Watchlist watchlist = new Watchlist();
        watchlist.setUser(user);
        return watchlistRepository.save(watchlist);
    }

    @Override
    public Watchlist findById(Long id) throws Exception {
        Optional<Watchlist> watchlistOptional = watchlistRepository.findById(id);
        return watchlistOptional.orElseThrow(() ->  new Exception("Not found"));
    }

    @Override
    public Coin addItemToWatchlist(Coin coin, UserEntity user) throws Exception {
        Watchlist watchlist = findUserWatchlist(user.getId());

        if (watchlist.getCoins().contains(coin)) {
            watchlist.getCoins().remove(coin);
        } else{
             watchlist.getCoins().add(coin);
        }

        watchlistRepository.save(watchlist);
        return coin;
    }
}
