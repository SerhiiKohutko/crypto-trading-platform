package org.example.tradingplatform.service;

import org.example.tradingplatform.modal.Asset;
import org.example.tradingplatform.modal.Coin;
import org.example.tradingplatform.modal.UserEntity;

import java.util.List;

public interface AssetService {
    Asset createAsset(UserEntity user, Coin coin, double quantity);

    Asset getAssetById(Long assetId) throws Exception;

    Asset getAssetByUserIdAndId(Long userId, Long assetId);

    List<Asset> getAssetsByUserId(Long userId);

    Asset updateAsset(Long assetId, double quantity) throws Exception;

    Asset findAssetByUserIdAndCoinId(Long userId, String coinId);

    void deleteAsset(Long assetId);
}
