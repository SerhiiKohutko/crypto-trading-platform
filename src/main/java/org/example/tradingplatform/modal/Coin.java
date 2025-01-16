package org.example.tradingplatform.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "coins")
public class Coin {
        @Id
        @JsonProperty("id")
        private String id;

        @JsonProperty("symbol")
        private String symbol;

        @JsonProperty("name")
        private String name;

        @JsonProperty("image")
        private String imageUrl;

        @JsonProperty("current_price")
        private double currentPrice;

        @JsonProperty("market_cap")
        private long marketCap;

        @JsonProperty("market_cap_rank")
        private Integer marketCapRank;

        @JsonProperty("fully_diluted_valuation")
        private BigDecimal fullyDilutedValuation;

        @JsonProperty("total_volume")
        private long totalVolume;

        @JsonProperty("high_24h")
        private double high24h;

        @JsonProperty("low_24h")
        private double low24h;

        @JsonProperty("price_change_24h")
        private double priceChange24h;

        @JsonProperty("price_change2_24h")
        private double priceChangePercentage24h;

        @JsonProperty("market_cap_change_24h")
        private long marketCapChange24h;

        @JsonProperty("market_cap_change_percentage_24h")
        private long marketCapChangePercentage24h;

        @JsonProperty("circulating_supply")

        private BigDecimal circulatingSupply;

        @JsonProperty("total_supply")

        private long totalSupply;

        @JsonProperty("max_supply")

        private BigDecimal maxSupply;

        @JsonProperty("ath")

        private BigDecimal allTimeHigh;

        @JsonProperty("ath_date")

        private LocalDateTime allTimeHighDate;

        @JsonProperty("atl")

        private BigDecimal allTimeLow;

        @JsonProperty("atl_date")

        private LocalDateTime allTimeLowDate;

        @JsonProperty("last_updated")

        private LocalDateTime lastUpdated;



        // Add getters and setters for each field similarly...

}
