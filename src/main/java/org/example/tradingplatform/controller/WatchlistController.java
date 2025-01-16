package org.example.tradingplatform.controller;

import org.example.tradingplatform.modal.Coin;
import org.example.tradingplatform.modal.UserEntity;
import org.example.tradingplatform.modal.Watchlist;
import org.example.tradingplatform.service.CoinService;
import org.example.tradingplatform.service.UserService;
import org.example.tradingplatform.service.WatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {
    @Autowired
    private WatchlistService watchlistService;
    @Autowired
    private UserService userService;
    @Autowired
    private CoinService coinService;

    @GetMapping("/user")
    public ResponseEntity<Watchlist> getUserWatchlist(@RequestHeader("Authorization") String jwt) throws Exception {
        UserEntity user = userService.findUserByJwt(jwt);

        Watchlist watchlist = watchlistService.findUserWatchlist(user.getId());
        return ResponseEntity.ok(watchlist);
    }
    @PostMapping("/{watchlist}")
    public ResponseEntity<Watchlist> getWatchlistById(@PathVariable Long watchList) throws Exception {
        Watchlist watchlist = watchlistService.findUserWatchlist(watchList);
        return ResponseEntity.ok(watchlist);
    }

    @PatchMapping("/add/coin/{coinId}")
    public ResponseEntity<Coin> addItemToWatchlist(@RequestHeader("Authorization") String jwt, @PathVariable String coinId) throws Exception {

        UserEntity user = userService.findUserByJwt(jwt);
        Coin coin = coinService.findById(coinId);
        Coin addedCoin = watchlistService.addItemToWatchlist(coin, user);
        return ResponseEntity.ok(addedCoin);

    }
}
