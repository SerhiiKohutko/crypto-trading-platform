package org.example.tradingplatform.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tradingplatform.modal.Coin;
import org.example.tradingplatform.service.CoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coins")
public class CoinController {
    @Autowired
    private CoinService coinService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<Coin>> getCoinList(@RequestParam("page") int page) throws Exception {
        List<Coin> coins = coinService.getCoinList(page);
        return new ResponseEntity<>(coins, HttpStatus.ACCEPTED);
    }


    @GetMapping("/{coinId}/chart")
    public ResponseEntity<JsonNode> getMarketChart(@PathVariable("coinId") String coinId,
                                                     @RequestParam("days") int days) throws Exception {

        String coins = coinService.getMarketChart(coinId, days);
        JsonNode node = objectMapper.readTree(coins);

        return new ResponseEntity<>(node, HttpStatus.ACCEPTED);
    }

    @GetMapping("/search")
    ResponseEntity<JsonNode> searchCoin(@RequestParam("q") String keyword) throws Exception {
        String coin = coinService.searchCoin(keyword);
        JsonNode node = objectMapper.readTree(coin);

        return new ResponseEntity<>(node, HttpStatus.ACCEPTED);
    }

    @GetMapping("/top50")
    ResponseEntity<JsonNode> getTop50CoinByMarketCapRank() throws Exception {
        String coins = coinService.getTop50CoinsByMarketCapRank();
        JsonNode node = objectMapper.readTree(coins);

        return new ResponseEntity<>(node, HttpStatus.ACCEPTED);
    }

    @GetMapping("/trending")
    ResponseEntity<JsonNode> getTreadingCoin() throws Exception {
        String coins = coinService.getTreadingCoins();
        JsonNode node = objectMapper.readTree(coins);

        return new ResponseEntity<>(node, HttpStatus.ACCEPTED);
    }


    @GetMapping("/details/{coinId}")
    ResponseEntity<JsonNode> getCoinDetails(@PathVariable String coinId) throws Exception {
        System.out.println(coinId);
        String coins = coinService.getCoinDetails(coinId);
        JsonNode node = objectMapper.readTree(coins);

        return new ResponseEntity<>(node, HttpStatus.ACCEPTED);
    }



}
